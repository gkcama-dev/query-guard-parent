package io.github.gkcamadev.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "queryguard")
public class QueryGuardProperties {

    private boolean enabled = true;// On-Off Lib ?
    private boolean blockDrop = true;// DROP TABLE block ?
    private boolean blockMassDelete = true;// No WHERE DELETE block ?

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBlockDrop() {
        return blockDrop;
    }

    public void setBlockDrop(boolean blockDrop) {
        this.blockDrop = blockDrop;
    }

    public boolean isBlockMassDelete() {
        return blockMassDelete;
    }

    public void setBlockMassDelete(boolean blockMassDelete) {
        this.blockMassDelete = blockMassDelete;
    }

}
