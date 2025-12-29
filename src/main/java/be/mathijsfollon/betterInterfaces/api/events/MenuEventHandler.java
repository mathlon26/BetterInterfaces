package be.mathijsfollon.betterInterfaces.api.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that handle menu events.
 * Methods annotated with this should accept a single parameter of type MenuEvent or a subclass.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuEventHandler {
    /**
     * The priority of this event handler.
     * Lower priority handlers are called first.
     *
     * @return the priority
     */
    int priority() default 0;

    /**
     * Whether to ignore cancelled events.
     *
     * @return true if cancelled events should be ignored
     */
    boolean ignoreCancelled() default false;
}
