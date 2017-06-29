package info.smart_tools.examples.items.items_feature.items_actor;

import info.smart_tools.examples.items.items_feature.items_actor.exception.ItemsActorException;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.AddNewItemWrapper;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.GetAllItemsWrapper;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemsActorTest {

    private ItemsActor actor;

    @Before
    public void init() {
        actor = new ItemsActor();
    }

    private List getListFromWrapper(final GetAllItemsWrapper mock) throws ChangeValueException {
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(mock).setAllItems(captor.capture());
        return captor.getValue();
    }

    @Test
    public void testAddOneItem() throws ItemsActorException, ChangeValueException, ReadValueException {
        GetAllItemsWrapper getAllWrapperBefore = mock(GetAllItemsWrapper.class);
        actor.getAllItems(getAllWrapperBefore);
        assertEquals(Collections.emptyList(), getListFromWrapper(getAllWrapperBefore));

        AddNewItemWrapper newItemWrapper = mock(AddNewItemWrapper.class);
        when(newItemWrapper.getNewItemName()).thenReturn("new item");
        actor.addNewItem(newItemWrapper);
        verify(newItemWrapper).getNewItemName();

        GetAllItemsWrapper getAllWrapperAfter = mock(GetAllItemsWrapper.class);
        actor.getAllItems(getAllWrapperAfter);
        List<String> expected = new ArrayList<>();
        expected.add("new item");
        assertEquals(expected, getListFromWrapper(getAllWrapperAfter));
    }

}
