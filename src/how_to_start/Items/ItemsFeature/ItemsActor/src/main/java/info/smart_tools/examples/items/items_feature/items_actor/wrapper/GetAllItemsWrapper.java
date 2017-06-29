package info.smart_tools.examples.items.items_feature.items_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

import java.util.List;


public interface GetAllItemsWrapper {

    /**
     * The Actors sets of list of all items here.
     * @throws ChangeValueException when the set fails
     */
    void setAllItems(final List<String> items)
            throws ChangeValueException;

}
