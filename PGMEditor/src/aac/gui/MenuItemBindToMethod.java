package aac.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Uma subclasse de JMenuItem que é instanciado associadamente a um método.
 * Quando o botão é chamado pelo swing, o método associado é invocado.
 * 
 * O método que será associado a este MenuItem deve, obrigatoriamente, ser
 * público, retornar void e não possuir nenhum parâmetro. Se estas condiações
 * não forem satisfeitas, uma exceção será levantada quando o botão for
 * pressionado.
 * 
 * O método pode ser de instância ou estático; isto é definido pelo construtor
 * que for chamado.
 */
public class MenuItemBindToMethod extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private Class<?> clazz;
	private Object instance;
	private String method;

	/**
	 * Cria um MenuItem que chamará um método estático quando pressionado.
	 * @param clazz Classe que possui o método estático.
	 * @param method Nome do método que será invocado.
	 * @param description Texto do botão na interface gráfica.
	 */
	public MenuItemBindToMethod(Class<?> clazz, String method, String description)
	{
		super(description);
		this.method = method;
		this.instance = null;
		this.clazz = clazz;
		this.addActionListener(this);
	}
	
	/**
	 * Cria um MenuItem que chamará um determinado método de instância quando
	 * pressionado.
	 * @param instance Instância que terá o método invocado.
	 * @param method Nome do método que será invocado.
	 * @param description Texto do botão na interface gráfica.
	 */
	public MenuItemBindToMethod(Object instance, String method, String description)
	{
		super(description);
		this.method = method;
		this.instance = instance;
		this.clazz = instance.getClass();
		this.addActionListener(this);
	}
	
	/**
	 * Implementação do método actionPerformed de ActionListener.
	 * Chamar este método irá invocar o método associado a este MenuItem.
	 */
	public void actionPerformed(ActionEvent event) {
		try {
			Method method = clazz.getMethod(this.method);
			method.invoke(instance, (Object[]) null);
		} catch (ReflectiveOperationException roe) {
			throw new RuntimeException(
					"Method " + this.method +
					" was not found in class " + this.clazz +
					" or couldn't be invoked from " +
					(instance != null ? "object " + instance : "static scope"), roe);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
}
