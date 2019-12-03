package com.riverisland.app.automation.tcplapi.productcatalogue.domain;

public class SwatchInfo {
	private boolean hasSwatches;
	private int swatchCount;
	public boolean isHasSwatches() {
		return hasSwatches;
	}
	public void setHasSwatches(boolean hasSwatches) {
		this.hasSwatches = hasSwatches;
	}
	public int getSwatchCount() {
		return swatchCount;
	}
	public void setSwatchCount(int swatchCount) {
		this.swatchCount = swatchCount;
	}
	@Override
	public String toString() {
		return "SwatchInfo [hasSwatches=" + hasSwatches + ", swatchCount=" + swatchCount + "]";
	}

}
