package api.hbm.energy;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IBatteryItem {

    void chargeBattery(ItemStack stack, long i);

    void setCharge(ItemStack stack, long i);

    void dischargeBattery(ItemStack stack, long i);

    long getCharge(ItemStack stack);

    long getMaxCharge();

    public long getChargeRate();

    public long getDischargeRate();

    /**
     * Returns a string for the NBT tag name of the long storing power
     */
    public default String getChargeTagName() {
        return "charge";
    }

    /**
     * Returns a string for the NBT tag name of the long storing power
     */
    public static String getChargeTagName(ItemStack stack) {
        return ((IBatteryItem) stack.getItem()).getChargeTagName();
    }

    /**
     * Returns an empty battery stack from the passed ItemStack, the original won't be modified
     */
    static ItemStack emptyBattery(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IBatteryItem) {
            String keyName = getChargeTagName(stack);
            ItemStack stackOut = stack.copy();
            CompoundTag tag;
            if (stack.hasTag())
                tag = stack.getTag();
            else
                tag = new CompoundTag();
            assert tag != null;
            tag.putLong(keyName, 0);
            stackOut.setTag(tag);
            return stackOut.copy();
        }
        return null;
    }

    /**
     * Returns an empty battery stack from the passed Item
     */
    public static ItemStack emptyBattery(Item item) {
        return item instanceof IBatteryItem ? emptyBattery(new ItemStack(item)) : null;
    }
}
