package script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Cay Horstmann
 * @version 1.04 2021-06-17
 */
public class ScriptTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // 当虚拟机启动时，它会发现可用的脚本引擎。为了枚举这些引擎，需要构建一个 ScriptEngineManager（未完）
                var manager = new ScriptEngineManager();
                String language;
                if (args.length == 0) {
                    System.out.println("Available factories: ");
                    // （接上）并调用 getEngineFactories() 方法
                    for (ScriptEngineFactory factory : manager.getEngineFactories()) {
                        System.out.println(factory.getEngineName());
                    }
                    language = "nashorn";
                } else {
                    language = args[0];
                }

                final ScriptEngine engine = manager.getEngineByName(language);
                if (engine == null) {
                    System.err.println("No engine for " + language);
                    System.exit(1);
                }

                final String frameClassName = args.length < 2 ? "buttons1.ButtonFrame" : args[1];
                var frame = (JFrame) Class.forName(frameClassName).getConstructor().newInstance();
                InputStream in = frame.getClass().getResourceAsStream("init." + language);
                if (in != null) {
                    engine.eval(new InputStreamReader(in, StandardCharsets.UTF_8));
                }
                var components = new HashMap<String, Component>();
                getComponentBindings(frame, components);
                // components.forEach((name, c) -> engine.put(name, c));
                components.forEach(engine::put);

                var events = new Properties();
                in = frame.getClass().getResourceAsStream(language + ".properties");
                if (in != null) {
                    events.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                }

                for (Object e : events.keySet()) {
                    String[] s = ((String) e).split("\\.");
                    addListener(s[0], s[1], (String) events.get(e), engine, components);
                }
                frame.setTitle("ScriptTest");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (ReflectiveOperationException | IOException | ScriptException | IntrospectionException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gathers all named components in a container.
     *
     * @param c               the component
     * @param namedComponents a map into which to enter the component names and components
     */
    private static void getComponentBindings(Component c,
                                             Map<String, Component> namedComponents) {
        String name = c.getName();
        if (name != null) {
            namedComponents.put(name, c);
        }
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                getComponentBindings(child, namedComponents);
            }
        }
    }

    /**
     * Adds a listener to an object whose listener method executes a script.
     *
     * @param beanName   the name of the bean to which the listener should be added
     * @param eventName  the name of the listener type, such as "action" or "change"
     * @param scriptCode the script code to be executed
     * @param engine     the engine that executes the code
     * @param components the bindings for the execution
     */
    private static void addListener(String beanName, String eventName, final String scriptCode,
                                    ScriptEngine engine, Map<String, Component> components)
            throws ReflectiveOperationException, IntrospectionException {
        Object bean = components.get(beanName);
        EventSetDescriptor descriptor = getEventSetDescriptor(bean, eventName);
        if (descriptor == null) {
            return;
        }
        descriptor.getAddListenerMethod().invoke(bean,
                Proxy.newProxyInstance(null, new Class[]{descriptor.getListenerType()},
                        (proxy, method, args) -> {
                            engine.eval(scriptCode);
                            return null;
                        }));
    }

    private static EventSetDescriptor getEventSetDescriptor(Object bean, String eventName)
            throws IntrospectionException {
        for (EventSetDescriptor descriptor : Introspector.getBeanInfo(bean.getClass()).getEventSetDescriptors()) {
            if (descriptor.getName().equals(eventName)) {
                return descriptor;
            }
        }
        return null;
    }
}
