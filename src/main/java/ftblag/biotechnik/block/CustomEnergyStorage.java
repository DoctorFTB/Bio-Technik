package ftblag.biotechnik.block;

import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public int setEnergy(int energy) {
        int temp = energy - this.energy;
        this.energy = energy;
        return temp;
    }
}
