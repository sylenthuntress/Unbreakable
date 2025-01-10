package sylenthuntress.unbreakable.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.ArrayList;
import java.util.List;

public class RepairMaterialRegistry {
    private static RepairMaterialRegistry instance = null;
    protected List<RegistryEntryList<Item>> repairMaterials = new ArrayList<>();

    private RepairMaterialRegistry() {

    }

    public static synchronized RepairMaterialRegistry getInstance() {
        if (instance == null)
            instance = new RepairMaterialRegistry();
        return instance;
    }

    public boolean isRepairMaterial(ItemStack stack) {
        for (RegistryEntryList<Item> registryEntryList : repairMaterials) {
            if (stack.isIn(registryEntryList)) return true;
        }
        return false;
    }

    public boolean isRepairMaterial(Item item) {
        return isRepairMaterial(item.getDefaultStack());
    }

    public void addRepairMaterial(RegistryEntryList<Item> items) {
        repairMaterials.add(items);
    }
}
