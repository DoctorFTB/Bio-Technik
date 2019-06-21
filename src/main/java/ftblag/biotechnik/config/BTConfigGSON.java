package ftblag.biotechnik.config;

import java.util.ArrayList;
import java.util.HashMap;

public class BTConfigGSON {

    ArrayList<String> blackListMobs = new ArrayList<>();
    HashMap<String, Integer> specificDrops = new HashMap<>();
    int maxAmount;
    int radius;
    String extractorToggleItem;
}
