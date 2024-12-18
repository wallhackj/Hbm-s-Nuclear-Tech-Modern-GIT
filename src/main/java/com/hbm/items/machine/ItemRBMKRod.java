package com.hbm.items.machine;

import java.util.List;

import com.hbm.interfaces.IItemHazard;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.modules.ItemHazardModule;
import com.hbm.tileentity.machine.rbmk.IRBMKFluxReceiver.NType;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;


public class ItemRBMKRod extends Item implements IItemHazard {

    public static final double xe135HalflifeMulPerTick = 0.9999241662036941; // 0.5^(1/9140) for a 9.14h halflife

    public ItemRBMKPellet pellet;
    public String fullName = "";            //full name of the fuel rod
    public double reactivity;                //endpoint of the function
    public double selfRate;                    //self-inflicted flux from self-igniting fuels
    public double archLength = 1000;        //used for arches of the function
    public EnumBurnFunc function = EnumBurnFunc.LOG_TEN;
    public EnumDepleteFunc depFunc = EnumDepleteFunc.GENTLE_SLOPE;
    public double xGen = 0.5D;                //multiplier for xenon production
    public double xBurn = 50D;                //divider for xenon burnup
    public double heat = 1D;                //heat produced per outFlux
    public double yield;                    //total potential inFlux the rod can take in its lifetime
    public double meltingPoint = 1000D;        //the maximum heat of the rod's hull before shit hits the fan. the core can be as hot as it wants to be
    public double diffusion = 0.02D;        //the speed at which the core heats the hull
    public NType nType = NType.SLOW;        //neutronType, the most efficient neutron type for fission
    public NType rType = NType.FAST;        //releaseType, the type of neutrons released by this fuel

    public float fuelR = 0.105F;
    public float fuelG = 0.247F;
    public float fuelB = 0.015F;
    public float cherenkovR = 0.4F;
    public float cherenkovG = 0.9F;
    public float cherenkovB = 1F;

    /*   _____
     * ,I I I I,
     * |'-----'|
     * |       |
     *  '-----'
     *  I I I I
     *  I I I I
     *  I I I I
     *  I I I I
     *  I I I I
     *  I I I I
     * ,I I I I,
     * |'-----'|
     * |       |
     *  '-----'
     *  I I I I
     *
     *  i drew a fuel rod yay
     */

    // Lol had quite some freetime

    public ItemRBMKRod(ItemRBMKPellet pellet, String s) {
        this(pellet.fullName, s);
        this.pellet = pellet;
    }

    public ItemRBMKRod(String fullName, String s) {
        this.setUnlocalizedName(s);
        this.setRegistryName(s);
        this.module = new ItemHazardModule();

        this.fullName = fullName;

        this.setContainerItem(ModItems.rbmk_fuel_empty);
        this.setMaxStackSize(1);
        this.setCreativeTab(MainRegistry.controlTab);

        ModItems.ALL_ITEMS.add(this);
    }

    public ItemRBMKRod setYield(double yield) {
        this.yield = yield;
        return this;
    }

    public ItemRBMKRod setStats(double funcEnd) {
        return setStats(funcEnd, 0);
    }

    public ItemRBMKRod setStats(double funcEnd, double selfRate) {
        this.reactivity = funcEnd;
        this.selfRate = selfRate;
        return this;
    }

    public ItemRBMKRod setStats(double funcEnd, double selfRate, double archLength) {
        this.reactivity = funcEnd;
        this.selfRate = selfRate;
        this.archLength = archLength;
        return this;
    }

    public ItemRBMKRod setFunction(EnumBurnFunc func) {
        this.function = func;
        return this;
    }

    public ItemRBMKRod setDepletionFunction(EnumDepleteFunc func) {
        this.depFunc = func;
        return this;
    }

    public ItemRBMKRod setXenon(double gen, double burn) {
        this.xGen = gen;
        this.xBurn = burn;
        return this;
    }

    public ItemRBMKRod setHeat(double heat) {
        this.heat = heat;
        return this;
    }

    public ItemRBMKRod setDiffusion(double diffusion) {
        this.diffusion = diffusion;
        return this;
    }

    public ItemRBMKRod setMeltingPoint(double meltingPoint) {
        this.meltingPoint = meltingPoint;
        return this;
    }

    public ItemRBMKRod setNeutronTypes(NType nType, NType rType) {
        this.nType = nType;
        this.rType = rType;
        return this;
    }

    public ItemRBMKRod setFuelColor(float R, float G, float B) {
        this.fuelR = R;
        this.fuelG = G;
        this.fuelB = B;
        return this;
    }

    public ItemRBMKRod setCherenkovColor(float R, float G, float B) {
        this.cherenkovR = R;
        this.cherenkovG = G;
        this.cherenkovB = B;
        return this;
    }

    /**
     * Adjusts the input flux using the poison level
     * Generates, then burns poison
     * Calculates the outflux based on influx, enrichment and poison
     * Depletes the yield, then returns the outflux
     *
     * @param stack
     * @param inFlux
     * @return outFlux
     */
    public double burn(Level world, ItemStack stack, double inFlux) {

        inFlux += selfRate;

        double xenon = getPoison(stack) * xe135HalflifeMulPerTick;
        xenon -= xenonBurnFunc(inFlux);

        inFlux *= (1D - getPoisonLevel(stack));

        xenon += xenonGenFunc(inFlux);

        if (xenon < 0D) xenon = 0D;
        if (xenon > 100D) xenon = 100D;

        setPoison(stack, xenon);

        double outFlux = reactivityFunc(inFlux, getEnrichment(stack)) * RBMKDials.getReactivityMod(world);

        double y = getYield(stack);
        y -= inFlux;

        if (y < 0D) y = 0D;

        setYield(stack, y);

        double coreHeat = getCoreHeat(stack);
        coreHeat += outFlux * heat;
        setCoreHeat(stack, coreHeat);

        return outFlux;
    }

    public static double getMeltdownFactor(double meltdownPercent) {
        if (meltdownPercent == 0) return 1;
        return 1D - 0.3D * (meltdownPercent / 100D);
    }

    /**
     * Heat up the core based on the outFlux, then move some heat to the hull
     *
     * @param stack
     */
    public void updateHeat(Level world, ItemStack stack, double mod) {

        double coreHeat = getCoreHeat(stack);
        double hullHeat = getHullHeat(stack);
        double meltdownPercent = getMeltdownPercent(stack);

        if (hullHeat > this.meltingPoint) {
            meltdownPercent += 0.05D * hullHeat / this.meltingPoint;
            setMeltdownPercent(stack, meltdownPercent);
        }

        if (coreHeat > hullHeat) {

            double mid = (coreHeat - hullHeat) / 2D;
            double heatTransfer = mid * this.diffusion * RBMKDials.getFuelDiffusionMod(world) * mod;
            coreHeat -= heatTransfer * getMeltdownFactor(meltdownPercent);
            hullHeat += heatTransfer;

            setCoreHeat(stack, coreHeat);
            setHullHeat(stack, hullHeat);
        }
    }

    /**
     * return one tick's worth of heat and cool the hull of the fuel rod, this heat goes into the fuel rod assembly block
     *
     * @param stack
     * @return
     */
    public double provideHeat(Level world, ItemStack stack, double heat, double mod) {

        double hullHeat = getHullHeat(stack);

        //metldown! the hull melts so the entire structure stops making sense
        //hull and fuel core heat, fuel skin heat are instantly averaged,
        //that average is sent to the component which is always fatal
        if (getMeltdownPercent(stack) >= 100) {
            setMeltdownPercent(stack, 100);
            double coreHeat = getCoreHeat(stack);
            double avg = (heat + hullHeat + coreHeat) / 3D;
            setCoreHeat(stack, avg);
            setHullHeat(stack, avg);
            return avg;
        }

        if (hullHeat <= heat)
            return 0;

        double ret = (hullHeat - heat) / 2;

        ret *= RBMKDials.getFuelHeatProvision(world) * mod;

        hullHeat -= ret;
        setHullHeat(stack, hullHeat);

        return ret;
    }

    public static enum EnumBurnFunc {
        PASSIVE("trait.rbmx.flux.passive"),                //const, no reactivity
        PLATEU("trait.rbmx.flux.euler"),                //(1 - e^(-x/25)) * reactivity * 100
        SIGMOID("trait.rbmx.flux.sigmoid"),                //100 / (1 + e^(-(x - 50) / 10)) <- tiny amount of reactivity at x=0 !
        LOG_TEN("trait.rbmx.flux.logten"),                //log10(x + 1) * reactivity * 50
        SQUARE_ROOT("trait.rbmx.flux.squrt"),            //sqrt(x) * 10 * reactivity
        ARCH("trait.rbmx.flux.arch"),                    //x-(x²/archLength) * reactivity
        LINEAR("trait.rbmx.flux.linear"),                //x * reactivity
        QUADRATIC("trait.rbmx.flux.quadratic"),            //x^2 / 100 * reactivity
        EXPERIMENTAL("trait.rbmx.flux.experimental");    //x * (sin(x) + 1)

        public String title = "";

        private EnumBurnFunc(String title) {
            this.title = title;
        }
    }

    /**
//     * @param flux [0;100] ...or at least those are sane levels
     * @return the amount of reactivity yielded, unmodified by xenon
     */
    public double reactivityFunc(double in, double enrichment) {

        double flux = in * reactivityModByEnrichment(enrichment);

        switch (this.function) {
            case PASSIVE:
                return selfRate * enrichment;
            case LOG_TEN:
                return Math.log10(flux + 1) * reactivity;
            case PLATEU:
                return (1 - Math.pow(Math.E, -flux / 25D)) * reactivity;
            case ARCH:
                return Math.max((flux - (flux * flux / archLength)) * reactivity, 0D);
            case SIGMOID:
                return reactivity / (1 + Math.pow(Math.E, -0.1D * flux + 5));
            case SQUARE_ROOT:
                return Math.sqrt(flux) * reactivity; //reactivity in decipercent
            case LINEAR:
                return flux * reactivity; //reactivity in percent
            case QUADRATIC:
                return flux * flux * reactivity; //reactivity in percent
            case EXPERIMENTAL:
                return flux * (Math.sin(flux) + 1) * reactivity;
        }

        return 0;
    }

    public String getFuncDescription(ItemStack stack) {

        String function;

        switch (this.function) {
            case PASSIVE:
                function = ChatFormatting.RED + "" + selfRate;
                break;
            case LOG_TEN:
                function = "log10(%1$s + 1) * %2$s";
                break;
            case PLATEU:
                function = "(1 - e^(-%1$s / 25)) * %2$s";
                break;
            case ARCH:
                function = "(%1$s - %1$s² / " + archLength + ") * %2$s";
                break;
            case SIGMOID:
                function = "%2$s / (1 + e^(-0.1 * %1$s + 5)";
                break;
            case SQUARE_ROOT:
                function = "sqrt(%1$s) * %2$s";
                break;
            case LINEAR:
                function = "%1$s * %2$s";
                break;
            case QUADRATIC:
                function = "%1$s² * %2$s";
                break;
            case EXPERIMENTAL:
                function = "%1$s * (sin(%1$s) + 1) * %2$s";
                break;
            default:
                function = "ERROR";
        }

        double enrichment = getEnrichment(stack);

        if (enrichment < 1) {
            enrichment = reactivityModByEnrichment(enrichment);
            String reactivity = ChatFormatting.YELLOW + "" + ((int) (this.reactivity * enrichment * 1000D) / 1000D) + ChatFormatting.WHITE;
            String enrichmentPer = ChatFormatting.GOLD + " (" + ((int) (enrichment * 1000D) / 10D) + "%)";

            return String.format(function, selfRate > 0 ? "(x" + ChatFormatting.RED + " + " + selfRate + "" + ChatFormatting.WHITE + ")" : "x", reactivity).concat(enrichmentPer);
        }

        return String.format(function, selfRate > 0 ? "(x" + ChatFormatting.RED + " + " + selfRate + "" + ChatFormatting.WHITE + ")" : "x", reactivity);
    }

    public static enum EnumDepleteFunc {
        LINEAR,            //old function
        RAISING_SLOPE,    //for breeding fuels such as MEU, maximum of 110% at 28% depletion
        BOOSTED_SLOPE,    //for strong breeding fuels such Th232, maximum of 132% at 64% depletion
        GENTLE_SLOPE,    //recommended for most fuels, maximum barely over the start, near the beginning
        STATIC;            //for arcade-style neutron sources
    }

    public double reactivityModByEnrichment(double enrichment) {

        switch (this.depFunc) {
            default:
            case LINEAR:
                return enrichment;
            case STATIC:
                return 1D;
            case BOOSTED_SLOPE:
                return enrichment + Math.sin((enrichment - 1) * (enrichment - 1) * Math.PI); //x + sin([x - 1]^2 * pi) works
            case RAISING_SLOPE:
                return enrichment + (Math.sin(enrichment * Math.PI) / 2D); //x + (sin(x * pi) / 2) actually works
            case GENTLE_SLOPE:
                return enrichment + (Math.sin(enrichment * Math.PI) / 3D); //x + (sin(x * pi) / 3) also works
        }
    }

    /**
     * Xenon generated per tick, linear function
     *
     * @param flux
     * @return
     */
    public double xenonGenFunc(double flux) {
        return flux * xGen;
    }

    /**
     * Xenon burned away per tick, quadratic function
     *
     * @param flux
     * @return
     */
    public double xenonBurnFunc(double flux) {
        return (flux * flux) / xBurn;
    }

    /**
     * @param stack
     * @return enrichment [0;1]
     */
    public static double getEnrichment(ItemStack stack) {
        return getYield(stack) / ((ItemRBMKRod) stack.getItem()).yield;
    }

    /**
     * @param stack
     * @return poison [0;1]
     */
    public static double getPoisonLevel(ItemStack stack) {
        return getPoison(stack) / 100D;
    }

//    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> list, TooltipFlag flag) {

        list.add(ChatFormatting.ITALIC + this.fullName);

        if (this == ModItems.rbmk_fuel_drx) {

            if (selfRate > 0 || this.function == EnumBurnFunc.SIGMOID) {
                list.add(ChatFormatting.RED + I18nUtil.resolveKey("trait.rbmx.source"));
            }

            list.add(ChatFormatting.GREEN + I18nUtil.resolveKey("trait.rbmx.depletion", ((int) (((yield - getYield(stack)) / yield) * 100000)) / 1000D + "%"));
            list.add(ChatFormatting.DARK_PURPLE + I18nUtil.resolveKey("trait.rbmx.xenon", ((int) (getPoison(stack) * 1000D) / 1000D) + "%"));
            list.add(ChatFormatting.BLUE + I18nUtil.resolveKey("trait.rbmx.splitsWith", I18nUtil.resolveKey(nType.unlocalized + ".x")));
            list.add(ChatFormatting.BLUE + I18nUtil.resolveKey("trait.rbmx.splitsInto", I18nUtil.resolveKey(rType.unlocalized + ".x")));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmx.fluxFunc", ChatFormatting.WHITE + getFuncDescription(stack)));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmx.funcType", I18nUtil.resolveKey(this.function.title)));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmx.xenonGen", ChatFormatting.WHITE + "x * " + xGen));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmx.xenonBurn", ChatFormatting.WHITE + "x² / " + xBurn));
            list.add(ChatFormatting.GOLD + I18nUtil.resolveKey("trait.rbmx.heat", heat + "°C"));
            list.add(ChatFormatting.GOLD + I18nUtil.resolveKey("trait.rbmx.diffusion", diffusion + "¹/²"));
            list.add(ChatFormatting.RED + I18nUtil.resolveKey("trait.rbmx.skinTemp", ((int) (getHullHeat(stack) * 10D) / 10D) + "m"));
            list.add(ChatFormatting.RED + I18nUtil.resolveKey("trait.rbmx.coreTemp", ((int) (getCoreHeat(stack) * 10D) / 10D) + "m"));
            list.add(ChatFormatting.DARK_RED + I18nUtil.resolveKey("trait.rbmx.melt", meltingPoint + "m"));
            list.add(ChatFormatting.DARK_RED + I18nUtil.resolveKey("trait.rbmx.meltdown", ((int) (getMeltdownPercent(stack) * 1000D) / 1000D) + "%"));

        } else {

            if (selfRate > 0 || this.function == EnumBurnFunc.SIGMOID) {
                list.add(ChatFormatting.RED + I18nUtil.resolveKey("trait.rbmk.source"));
            }

            list.add(ChatFormatting.GREEN + I18nUtil.resolveKey("trait.rbmk.depletion", ((int) (((yield - getYield(stack)) / yield) * 100000D)) / 1000D + "%"));
            list.add(ChatFormatting.DARK_PURPLE + I18nUtil.resolveKey("trait.rbmk.xenon", ((int) (getPoison(stack) * 1000D) / 1000D) + "%"));
            list.add(ChatFormatting.BLUE + I18nUtil.resolveKey("trait.rbmk.splitsWith", I18nUtil.resolveKey(nType.unlocalized)));
            list.add(ChatFormatting.BLUE + I18nUtil.resolveKey("trait.rbmk.splitsInto", I18nUtil.resolveKey(rType.unlocalized)));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmk.fluxFunc", ChatFormatting.WHITE + getFuncDescription(stack)));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmk.funcType", I18nUtil.resolveKey(this.function.title)));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmk.xenonGen", ChatFormatting.WHITE + "x * " + xGen));
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmk.xenonBurn", ChatFormatting.WHITE + "x² / " + xBurn));
            list.add(ChatFormatting.GOLD + I18nUtil.resolveKey("trait.rbmk.heat", heat + "°C"));
            list.add(ChatFormatting.GOLD + I18nUtil.resolveKey("trait.rbmk.diffusion", diffusion + "¹/²"));
            list.add(ChatFormatting.RED + I18nUtil.resolveKey("trait.rbmk.skinTemp", ((int) (getHullHeat(stack) * 10D) / 10D) + "°C"));
            list.add(ChatFormatting.RED + I18nUtil.resolveKey("trait.rbmk.coreTemp", ((int) (getCoreHeat(stack) * 10D) / 10D) + "°C"));
            list.add(ChatFormatting.DARK_RED + I18nUtil.resolveKey("trait.rbmk.melt", meltingPoint + "°C"));
            list.add(ChatFormatting.DARK_RED + I18nUtil.resolveKey("trait.rbmk.meltdown", ((int) (getMeltdownPercent(stack) * 1000D) / 1000D) + "%"));
        }

        super.addInformation(stack, worldIn, list, flag);
        updateModule(stack);
        this.module.addInformation(stack, list, flag);
    }

    @Override
    public void onUpdate(ItemStack stack, Level world, Entity entity, int i, boolean b) {

        if (entity instanceof LivingEntity) {
            updateModule(stack);
            this.module.applyEffects((LivingEntity) entity, stack.getCount(), i, b,
                    ((LivingEntity) entity).getMainHandItem() == stack ?
                            InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem item) {

        super.onEntityItemUpdate(item);
        updateModule(item.getItem());
        return this.module.onEntityItemUpdate(item);
    }

    ItemHazardModule module;

    @Override
    public ItemHazardModule getModule() {
        return this.module;
    }

    private void updateModule(ItemStack stack) {

        float mod = (float) (1 + (1 - getEnrichment(stack)) * 24 + getPoisonLevel(stack) * 100);
        this.module.setMod(mod);
    }

    /*  __    __   ____     ________
     * |  \  |  | |  __ \  |__    __|
     * |   \ |  | | |__| |    |  |
     * |  |\\|  | |  __ <     |  |
     * |  | \   | | |__| |    |  |
     * |__|  \__| |_____/     |__|
     */

    public static void setYield(ItemStack stack, double yield) {
        setDouble(stack, "yield", yield);
    }

    public static double getYield(ItemStack stack) {

        if (stack.getItem() instanceof ItemRBMKRod) {
            return getDouble(stack, "yield");
        }

        return 0;
    }

    public static void setMeltdownPercent(ItemStack stack, double meltdownPercent) {
        setDouble(stack, "meltdown", meltdownPercent);
    }

    public static double getMeltdownPercent(ItemStack stack) {
        return getDouble(stack, "meltdown");
    }

    public static void setPoison(ItemStack stack, double xenon) {
        setDouble(stack, "xenon", xenon);
    }

    public static double getPoison(ItemStack stack) {
        return getDouble(stack, "xenon");
    }

    public static void setCoreHeat(ItemStack stack, double heat) {
        setDouble(stack, "core", heat);
    }

    public static double getCoreHeat(ItemStack stack) {
        return getDouble(stack, "core");
    }

    public static void setHullHeat(ItemStack stack, double heat) {
        setDouble(stack, "hull", heat);
    }

    public static double getHullHeat(ItemStack stack) {
        return getDouble(stack, "hull");
    }

//    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getDurabilityForDisplay(stack) > 0D;
    }

//    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - getEnrichment(stack);
    }

    public static void setDouble(ItemStack stack, String key, double yield) {

        if (!stack.hasTag())
            setNBTDefaults(stack);

        stack.getTag().putDouble(key, yield);
    }

    public static double getDouble(ItemStack stack, String key) {

        if (!stack.hasTag())
            setNBTDefaults(stack);

        return stack.getTag().getDouble(key);
    }

    /**
     * Sets up the default values for all NBT data because doing it one-by-one will only correctly set the first called value and the rest stays 0 which is very not good
     *
     * @param stack
     */
    private static void setNBTDefaults(ItemStack stack) {
        stack.setTag(new CompoundTag());
        setYield(stack, ((ItemRBMKRod) stack.getItem()).yield);
        setCoreHeat(stack, 20.0D);
        setHullHeat(stack, 20.0D);
        setMeltdownPercent(stack, 0D);
    }
}