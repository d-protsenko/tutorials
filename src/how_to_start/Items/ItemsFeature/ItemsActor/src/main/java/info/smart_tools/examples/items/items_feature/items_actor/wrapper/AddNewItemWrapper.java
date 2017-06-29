package info.smart_tools.examples.items.items_feature.items_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface AddNewItemWrapper {

    /**
     * The gets the new item name here.
     * @return the new item name
     * @throws ReadValueException when the get fails
     */
    String getNewItemName()
            throws ReadValueException;
}
