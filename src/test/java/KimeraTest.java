/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.core.controller.HibernateUtil;
import com.core.controller.Kimera;
import com.core.entities.Numeros;
import com.core.entities.Recarga;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Gonza
 */
public class KimeraTest {
    
    private Kimera k;
    
    public KimeraTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        k = new Kimera();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     @Ignore
     public void first() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        List<Numeros> l = s.createCriteria(Numeros.class).list();
        s.close();
        Iterator<Numeros> i = l.iterator();
        while(i.hasNext()){
            Numeros n = i.next();
            System.out.println(n.getNumero());
        }
     }
     @Test
     @Ignore
     public void second(){
         Recarga r = k.byId("nroVenta", 181241356L, Recarga.class);
         assertNotNull(r);
         System.out.println(r.getDestino());
         Numeros n = k.byId("numero", "57000747657534016", Numeros.class);
         assertNotNull(n);
         System.out.println("Numero encontrado: " + n.getNumero() + ". Empresa: " + n.getOperadora());
     }
     @Test
     public void remove(){
         Numeros n = k.byId("numero", "57000747657534016", Numeros.class);
         Boolean removido = k.remove(n);
         assertTrue(removido);
         System.out.println("Removido exitosamente");
     }
     
}
