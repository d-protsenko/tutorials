package info.smart_tools.examples.items.items_feature.items_actor_plugin;

import info.smart_tools.examples.items.items_feature.items_actor.ItemsActor;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

public class ItemsActorPlugin extends BootstrapPlugin {

     /**
     * Constructs the plugin.
     * @param bootstrap the bootstrap instance
     */
    public ItemsActorPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("items-actor-plugin")     // the unique name of the plugin item, the items may depend on each other
    public void init()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("ItemsActor"),    // the unique name of the actor in IOC
                new ApplyFunctionToArgumentsStrategy(
                        a -> {
                            try {
                                return new ItemsActor();
                            } catch (Exception e) {
                                throw new FunctionExecutionException(e);
                            }
                        }
                )
        );
    }
}
