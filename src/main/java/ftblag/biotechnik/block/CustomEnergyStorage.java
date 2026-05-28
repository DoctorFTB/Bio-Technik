package ftblag.biotechnik.block;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public class CustomEnergyStorage extends SimpleEnergyHandler {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public int setEnergy(int energy) {
        int temp = energy - this.energy;
        this.energy = energy;
        return temp;
    }
}
