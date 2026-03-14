package edu.pucmm.icc352.config;

import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {

    private static SessionFactory sessionFactory;
    private static Server h2Server;

    public static void init() {
        try {
            // Arranca H2 en modo servidor automáticamente
            h2Server = Server.createTcpServer(
                    "-tcp",
                    "-tcpAllowOthers",
                    "-tcpPort", "9092",
                    "-ifNotExists"
            ).start();
            System.out.println("H2 Server corriendo en puerto 9092");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo iniciar H2: " + e.getMessage());
        }

        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

        System.out.println("Hibernate conectado a H2");
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) sessionFactory.close();
        if (h2Server != null) h2Server.stop();
    }
}