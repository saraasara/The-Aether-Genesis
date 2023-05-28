package com.aetherteam.aether_genesis.entity.passive;

import com.aetherteam.aether_genesis.GenesisTags;
import com.aetherteam.aether_genesis.client.GenesisSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CarrionSprout extends Mob {
    public static final EntityDataAccessor<Integer> DATA_SIZE_ID = SynchedEntityData.defineId(CarrionSprout.class, EntityDataSerializers.INT);

    public float sinage;
    public float sinageAdd;

    public CarrionSprout(EntityType<? extends CarrionSprout> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    //todo look around and look at player ai

    @Nonnull
    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SIZE_ID, 0);
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> dataAccessor) {
        if (DATA_SIZE_ID.equals(dataAccessor)) {
            this.setBoundingBox(this.makeBoundingBox());
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor level, @Nonnull DifficultyInstance difficulty, @Nonnull MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        this.setPos(Math.floor(this.getX()) + 0.5, this.getY(), Math.floor(this.getZ()) + 0.5);
        this.setSize(this.random.nextInt(4) + 1);
        this.sinage = this.random.nextFloat() * 6.0F;
        return super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
    }

    public static boolean checkCarrionSproutSpawnRules(EntityType<? extends CarrionSprout> carrionSprout, LevelAccessor level, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && level.getBlockState(pos.below()).is(GenesisTags.Blocks.CARRION_SPROUT_SPAWNABLE_ON)
                && level.getRawBrightness(pos, 0) > 8
                && (spawnReason != MobSpawnType.NATURAL || random.nextInt(10) == 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.getBlockState(this.blockPosition().below()).is(GenesisTags.Blocks.CARRION_SPROUT_SPAWNABLE_ON)) {
            this.kill();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.sinage += this.sinageAdd;
        if (this.hurtTime > 0) {
            this.sinageAdd = 0.9F;
        } else {
            this.sinageAdd = 0.15F;
        }
        if (this.sinage >= Mth.TWO_PI) {
            this.sinage -= Mth.TWO_PI;
        }
    }

    @Override
    protected void jumpFromGround() { }

    @Override
    protected void doPush(@Nonnull Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d0 = entity.getX() - this.getX();
                double d1 = entity.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= (double) 0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0 / d2;
                    if (d3 > 1.0) {
                        d3 = 1.0;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05F;
                    d1 *= 0.05F;

                    if (!entity.isVehicle()) {
                        entity.push(d0, 0.0, d1);
                    }
                }
            }
        }
    }

    //todo: sizing code
//    @Nonnull
//    @Override
//    protected AABB makeBoundingBox() {
//        float width = 0.75F + this.getSize() * 0.125F;
//        float height = 0.5F + this.getSize() * 0.075F;
//        EntityDimensions newDimensions = EntityDimensions.fixed(width, height);
//        return newDimensions.makeBoundingBox(this.position());
//    }

    public int getSize() {
        return this.entityData.get(DATA_SIZE_ID);
    }

    public void setSize(int size) {
        this.entityData.set(DATA_SIZE_ID, size);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return GenesisSoundEvents.ENTITY_CARRION_SPROUT_DEATH.get();
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose pose, @Nonnull EntityDimensions size) {
        return 0.5F;
    } //todo

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Size", this.getSize());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Size")) {
            this.setSize(tag.getInt("Size"));
        }
    }
}

