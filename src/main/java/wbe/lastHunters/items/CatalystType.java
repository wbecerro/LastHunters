package wbe.lastHunters.items;

import java.util.List;

public class CatalystType {

    private String id;

    private String signature;

    private String name;

    private List<String> lore;

    public CatalystType(String id, String signature, String name, List<String> lore) {
        this.id = id;
        this.signature = signature;
        this.name = name;
        this.lore = lore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
