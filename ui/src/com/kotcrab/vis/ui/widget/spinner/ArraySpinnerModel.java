package com.kotcrab.vis.ui.widget.spinner;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * {@link Spinner} model allowing to browse through items from object {@link Array}.
 * <p>
 * Note that this (by default) uses item's toString() method to get string representation of objects used to validate
 * that user has entered valid value which due to {@link VisValidatableTextField} nature has to be done for every
 * entered letter. Item's toString() should cache it's result internally to optimize this check. To customize how string
 * representation is obtained override {@link #itemToString(Object)}.
 * @author Kotcrab
 * @since 1.0.2
 */
public class ArraySpinnerModel<T> implements SpinnerModel {
	private Spinner spinner;

	private Array<T> items = new Array<T>();
	private T current;
	private int currentIndex;

	/**
	 * Creates empty instance with no items set. Note that spinner with empty array model will be always treated as in
	 * invalid state.
	 */
	public ArraySpinnerModel () {
	}

	/**
	 * Creates new instance of {@link ArraySpinnerModel} using provided items.
	 * @param items array containing items for the model. It is copied to new array in order to prevent accidental
	 * modification. Array may be empty however in such case spinner will be always in invalid input state.
	 */
	public ArraySpinnerModel (Array<T> items) {
		this.items.addAll(items);
	}

	@Override
	public void bind (Spinner spinner) {
		if (this.spinner != null)
			throw new IllegalStateException("ArraySpinnerModel can be only used by single instance of Spinner");
		this.spinner = spinner;
		setCurrent(0);
		spinner.getTextField().addValidator(new InputValidator() {
			@Override
			public boolean validateInput (String input) {
				return getItemIndexForText(input) != -1;
			}
		});
		spinner.notifyValueChanged(true);
	}

	/**
	 * Creates string representation displayed in {@link Spinner} for given object. By default toString() is used.
	 * @param item that string representation should be created. It is necessary to check if item is null!
	 * @return string representation of item
	 */
	protected String itemToString (T item) {
		if (item == null) return "";
		return item.toString();
	}

	private int getItemIndexForText (String text) {
		for (int i = 0; i < items.size; i++) {
			T item = items.get(i);
			if (itemToString(item).equals(text)) return i;

		}

		return -1;
	}

	@Override
	public void textChanged () {
		String text = spinner.getTextField().getText();
		int index = getItemIndexForText(text);
		if (index == -1) return;
		setCurrent(index);
	}

	@Override
	public boolean increment () {
		if (currentIndex + 1 >= items.size) return false;
		setCurrent(currentIndex + 1);
		return true;
	}

	@Override
	public boolean decrement () {
		if (currentIndex - 1 < 0) return false;
		setCurrent(currentIndex - 1);
		return true;
	}

	@Override
	public String getText () {
		return itemToString(current);
	}

	/** Notifies model that items has changed and view must be refreshed. This will trigger a change event. */
	public void invalidateDataSet () {
		setCurrent(MathUtils.clamp(currentIndex, 0, items.size - 1));
		spinner.notifyValueChanged(true);
	}

	/** @return array containing model items. If you modify returned array you must call {@link #invalidateDataSet()}. */
	public Array<T> getItems () {
		return items;
	}

	/** Changes items of this model. Current index is not preserved. This will trigger a change event. */
	public void setItems (Array<T> newItems) {
		items.clear();
		items.addAll(newItems);
		currentIndex = 0;
		invalidateDataSet();
	}

	/** @return current item index or -1 if items array is empty */
	public int getCurrentIndex () {
		return currentIndex;
	}

	/** @return current item or null if items array is empty */
	public T getCurrent () {
		return current;
	}

	/** Sets current item. If array is empty then current value will be set to null. */
	public void setCurrent (int newIndex) {
		if (items.size == 0) {
			current = null;
			currentIndex = -1;
		} else {
			currentIndex = newIndex;
			current = items.get(newIndex);
		}
	}

	/** @param item if does not exist in items array, model item will be set to first item. */
	public void setCurrent (T item) {
		int index = items.indexOf(item, true);
		if (index == -1) {
			setCurrent(0);
		} else {
			setCurrent(index);
		}
	}
}