# Proyecto Kimera
Artefacto encargado de conectarse a una base de datos y realizar las tipicas operaciones CRUD de manera simple y eficiente.

# Motivación
Separar la lógica de negocios de la capa de acceso a datos, utilizando la minima cantidad de lineas de código.

# Requisitos
JBoss Java EE 6 Specification APIs
```xml
<dependency>
      <groupId>org.jboss.spec</groupId>
      <artifactId>jboss-javaee-6.0</artifactId>
      <version>1.0.0.Final</version>
      <type>pom</type>
      <scope>provided</scope>
  </dependency>
```
Hibernate Entity Manager
```xml
<dependency>
      <groupId>org.hibernate</groupId>
      <artifactId>hibernate-entitymanager</artifactId>
      <version>4.3.1.Final</version>
</dependency>
```
```xml
<dependency>
      <groupId>org.hibernate.javax.persistence</groupId>
      <artifactId>hibernate-jpa-2.1-api</artifactId>
      <version>1.0.0.Final</version>
</dependency>
```
## Aclaración
Los test unitarios se realizaron con una base de datos PostgreSQL con el siguiente driver
```xml
<dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <version>9.2-1003-jdbc4</version>
  </dependency>
```
# Hibernate Util
Clase necesaria para crear la conexión a la Base de Datos. Se puede notar que la libreria esta preparada para ser implementada en un servidor Openshift, cuando corre en la máquina local debe remover el datasource que ofrece el servidor Openshift. Si quieres utilizar tu propio datasource solo comenta las lineas del "if(System...){...}
```java
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            Configuration config = new Configuration();
            config.configure();
            if (System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST") == null) {
                config.getProperties().remove("hibernate.connection.datasource");
            }
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
            sessionFactory = config.buildSessionFactory(builder.build());
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
```
# Un caso de uso
Actualmente tengo una base de datos de todas las recargas de saldo virtual realizados en el comercio de mis padres, se ve algo así:
![Tabla Recargas](http://i.imgur.com/Qny3xNO.png "Recargas")
Por otro lado tengo una tabla que cree para guardar todos los números móviles de los clientes 
(sacados de la tabla Recargas)<br />
![Tabla Numeros](http://i.imgur.com/wcxXMOy.png "Numeros") <br />
A partir de aqui se necesitan generar los archivos Hibernate <br />
1. hibernate.cfg.xml <br />
2. hibernate.revenge.xml <br />
3. Entities POJO generados con Hibernate Tools 4.3.1 <br />

Con eso podemos realizar el siguiente Test Unitario
# La hora del Test
Vamos a obtener un Objeto "Recarga" y otro "Numeros" de la base de datos "recargas"
```java
    @Test
    public void kimeraTest(){
        Kimera k = new Kimera();
        Recarga r = k.byId("nroVenta", 181241356L, Recarga.class);
        assertNotNull(r);
        System.out.println(r.getDestino());
        Numeros n = k.byId("numero", "57000747657534016", Numeros.class);
        assertNotNull(n);
        System.out.println("Numero encontrado: " + n.getNumero() + ". Empresa: " + n.getOperadora());
    }
```
Cuya salida es:
```
3794894328
Numero encontrado: 57000747657534016. Empresa: DIRECTV (3)
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.807 sec
```
# Métodos de Kimera
**all(Class type)**: devuelve una lista del tipo List<type> con todos los objetos de la clase type (SELECT * FROM type as TABLE)<br />
**byId(String key,Object value, Class type)**: obtiene un objeto de la clase type a partir del campo "key" y el valor "value"<br />
**remove(Object obj)**: elimina un objeto entitie de la base de datos<br />
**add(Object obj)**: agrega un nuevo objeto a la base de datos<br />
**update(Object obj)**: actualiza un objeto existente en la base de datos<br />
**getLast(String key, Class type)**: obtiene el ultimo objeto del tipo class declarado agregado en la base de datos (según su orden interno) a partir de la clave pasada como parámetro.<br />
**byRestrictions(List<Criterion> restrictions, Class type)**: obtiene un objeto del tipo class declarado a partir de una lista de restricciones del tipo Criterion<br />
**listByRestrictions(List<Criterion> restrictions, Class type)**: obtiene una lista de objetos del tipo class declarado a partir de una lista de restricciones del tipo Criterion<br />
**withRestrictions(Map<String,Object> restrictions, Class type)**: obtiene una lista de objetos del tipo class declarado a partir de una lista de restricciones<br />
**withParams(Map<String,Object> restrictions,Class type)**: obtiene una lista de objetos del tipo class declarado a partir de una lista de restricciones<br />
**withRestrictionsLike(Map<String,Object> restrictions, Class type)**: obtiene una lista de objetos del tipo class declarado a partir de un mapa de restricciones con restricciones LIKE concatenados según el operador AND<br />
**byIdLike(String key, String value, Class type)**: mismo funcionamiento que el método "byId(...)" pero con el operador LIKE<br />
**allOrderBy(String key, OrderBy order, Class type)**: obtiene una lista de objetos del tipo class declarado ordenado de manera ascendente o descendiente<br />
**withRestrictionsOrderBy(Map<String,Object> restrictions,String key, OrderBy order, Class type)**: obtiene una lista de objetos del tipo class declarado a partir de un mapa de restricciones ordenado segun el objeto "order"<br />
**withRestrictions(List<Criterion> restrictions, Class type)**: obtiene una lista de objetos del tipo class declarado a partir de una lista de restricciones del tipo Criterion<br />
**callProcedure(String query, Map<String,Object> params)**: realiza una llamada de bajo nivel a la base de datos, muy útil para ejecutar procedimientos almacenados, seleccionar vistas u otras operaciones complejas<br />
**betweenDates(String field, Date first, Date second, Class type)**: obtiene una lista de objetos del tipo class declarado a partir de dos fechas. La tabla debe contener una columna de tipo date.<br />
# Licencia
MIT
# Agradecimientos
A los integrantes del equipo de [Div-ID Software](https://www.facebook.com/dividsoftware)
