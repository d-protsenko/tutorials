package info.smart_tools.examples.items.items_feature.items_actor;

import info.smart_tools.examples.items.items_feature.items_actor.exception.ItemsActorException;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.AddNewItemWrapper;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.GetAllItemsWrapper;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemsActor {

    private final List<String> items = new ArrayList<>();

    /**
     * Retrieves the list of all items.
     * @param wrapper the wrapper where to set the list
     * @throws ItemsActorException if something goes wrong
     */
    public void getAllItems(final GetAllItemsWrapper wrapper) throws ItemsActorException {
        try {
            wrapper.setAllItems(Collections.unmodifiableList(items));
        } catch (ChangeValueException e) {
            throw new ItemsActorException("Failed to set list", e);
        }
    }

    /**
     * Add the new item to the list.
     * @param wrapper the wrapper where to get the name of the new item
     * @throws ItemsActorException if something goes wrong
     */
    public void addNewItem(final AddNewItemWrapper wrapper) throws ItemsActorException {
        try {
            items.add(wrapper.getNewItemName());
        } catch (ReadValueException e) {
            throw new ItemsActorException("Failed to get item name", e);
        }
    }

}
