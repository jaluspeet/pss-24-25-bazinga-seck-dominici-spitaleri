package it.unibo.pss.controller.model;

/** Observer interface for model updates. */
public interface ModelObserver {

	/** Called when the model is updated. */
	void onModelUpdated();
}
