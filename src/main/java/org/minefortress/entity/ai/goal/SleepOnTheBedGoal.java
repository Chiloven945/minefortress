package org.minefortress.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.minefortress.entity.Colonist;
import org.minefortress.fortress.FortressBedInfo;
import org.minefortress.fortress.FortressServerManager;

import java.util.Optional;

public class SleepOnTheBedGoal extends Goal {

    private final Colonist colonist;
    private FortressBedInfo bedInfo;

    public SleepOnTheBedGoal(Colonist colonist) {
        this.colonist = colonist;
    }

    @Override
    public boolean canStart() {
        if(!isNight() || !colonist.doesNotHaveAnyOtherTask()) return false;

        final Optional<FortressBedInfo> freeBedOptional = getFreeBed();
        return freeBedOptional.isPresent();
    }

    @Override
    public void start() {
        final Optional<FortressBedInfo> freeBed = getFreeBed();
        if(freeBed.isPresent()) {
            bedInfo = freeBed.get();
            bedInfo.setOccupied(true);
            moveToBed();
        }
    }

    @Override
    public void tick() {
        if(colonist.getNavigation().isIdle()) {
            if(hasReachedTheBed()) {
                ///TODO: Sleep
            } else {
                moveToBed();
            }
        }
    }

    private void moveToBed() {
        if(bedInfo == null) return;
        final BlockPos pos = bedInfo.getPos();
        final EntityNavigation navigation = colonist.getNavigation();
        final Path pathTo = navigation.findPathTo(pos, 1);
        navigation.startMovingAlong(pathTo, 1.5);
    }

    private boolean hasReachedTheBed() {
        if(bedInfo == null) return true;
        final BlockPos pos = bedInfo.getPos();
        return Math.sqrt(colonist.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) < 2;
    }

    @Override
    public boolean shouldContinue() {
        return isNight() && bedInfo != null && colonist.doesNotHaveAnyOtherTask();
    }

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public void stop() {
        colonist.getNavigation().stop();
        this.bedInfo = null;
    }

    @NotNull
    private Optional<FortressBedInfo> getFreeBed() {
        return colonist
                .getFortressServerManager().flatMap(FortressServerManager::getFreeBed);
    }

    private boolean isNight() {
        return colonist.world.isNight();
    }

}