package io.github.evgenyjdev.deepcopy;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/*
This class for deep cloning objects.
 Works without additional JVM arguments in Java8
 Due to restrictions on access to private fields of JavaSE library classes Java17 requires additional VM arguments like these:
 --add-opens java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED
 --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED
 */
public class Cloner {

    private boolean forceInstantiate = false;

    public void setForceInstantiate(boolean forceInstantiate) {
        this.forceInstantiate = forceInstantiate;
    }

    public Cloner(boolean forceInstantiate) {
        this.forceInstantiate = forceInstantiate;
    }

    public Cloner() {
    }

    private  Object createEmptyObject(Class<?> sourceClass, Object source) {
        // If there is a no-argument constructor
        List<Constructor<?>> declaredConstructors = Arrays.asList(sourceClass.getDeclaredConstructors());
        Constructor<?> noArgConstructor = declaredConstructors.stream()
                .filter(c -> c.getParameterTypes().length == 0)
                .findAny().orElse(null);
        if (noArgConstructor != null) {
            try {
                noArgConstructor.setAccessible(true);
                return noArgConstructor.newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(String.format("Unable to instantiate class %s with constructor", sourceClass), e);
            }
        } else if (forceInstantiate) {
            if (source instanceof Serializable) {
                try {
                    ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(sourceClass);

                    Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance");
                    newInstance.setAccessible(true);
                    return newInstance.invoke(objectStreamClass);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    throw new RuntimeException(String.format("Unable to instantiate serializable class %s", sourceClass), e);
                }
            } else {
                // To create a new object, we use the constructor with the shortest number of arguments
                Constructor<?> selectedConstructor = declaredConstructors.stream()
                        .min(Comparator.comparing(x -> x.getParameterTypes().length))
                        .orElse(null);

                if (selectedConstructor == null) {
                    throw new RuntimeException(String.format("Unable to determine constructor for class %s", sourceClass));
                }
                selectedConstructor.setAccessible(true);
                List<Object> params = new ArrayList<>();
                for (Class<?> clazz : selectedConstructor.getParameterTypes()) {
                    if (clazz.isPrimitive()) {
                        switch (clazz.getName()) {
                            case "char":
                                params.add('a');
                                break;
                            case "boolean":
                                params.add(false);
                                break;
                            case "byte":
                            case "short":
                            case "int":
                            case "long":
                            case "float":
                            case "double":
                                params.add((byte) 0);
                                break;
                        }
                    } else if (clazz.isArray()) {
                        params.add(java.lang.reflect.Array.newInstance(clazz.getComponentType(), 0));
                    } else {
                        params.add(null);
                    }
                }
                try {
                    return selectedConstructor.newInstance(params.toArray());
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(String.format("Unable to instantiate class %s with constructor", sourceClass), e);
                }
            }
        }
        return null;
    }

    private Object cloneObject(Object source, Map<Object, Object> objects) {
        if (source == null) {
            return null;
        }
        Object copied = objects.get(source); // this object has already been copied
        if (copied != null) {
            return copied; // => return link to copy
        }
        Class<?> sourceClass = source.getClass();
        Object target;
        if (sourceClass.isArray()) {
            int length = java.lang.reflect.Array.getLength(source);
            target = java.lang.reflect.Array.newInstance(sourceClass.getComponentType(), length);
            objects.put(source, target);
            if (sourceClass.getComponentType().isPrimitive()) {
                for (int i = 0; i < length; i++) {
                    java.lang.reflect.Array.set(target, i, java.lang.reflect.Array.get(source, i));
                }
            } else {
                for (int i = 0; i < length; i++) {
                    java.lang.reflect.Array.set(target, i, cloneObject(java.lang.reflect.Array.get(source, i), objects));
                }
            }
        } else {
            target = createEmptyObject(sourceClass, source);
            objects.put(source, target);
            if (target != null) {
                Field[] fields = sourceClass.getDeclaredFields();
                try {
                    for (Field field : fields) {
                        if (!Modifier.isStatic(field.getModifiers())) {
                            field.setAccessible(true);
                            Object value = field.get(source);
                            if (!field.getType().isPrimitive()) {
                                value = cloneObject(value, objects);
                            }
                            field.set(target, value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Unable to get/set property for class %s", sourceClass), e);
                }
            }
        }
        return target;
    }

    public <T> T deepCopy(T source) {
        Map<Object, Object> clonedObjects = new HashMap<>(); // Map: original object -> copy of the original object
        T copy = (T) cloneObject(source, clonedObjects);
        return copy;
    }
}
