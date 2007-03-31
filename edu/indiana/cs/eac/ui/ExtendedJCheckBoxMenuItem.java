package edu.indiana.cs.eac.ui;

import javax.swing.JCheckBoxMenuItem;

/**
 * Adds an object reference to a normal <code>JCheckBoxMenuItem</code>.
 * 
 * <p>For dynamically generated menus, it is often helpful to tie a menu
 * item to a particular object.  This class extends the standard
 * <code>JCheckBoxMenuItem</code> by adding a private <code>Object</code> 
 * field.  That way, when an <code>ActionEvent</code> is fired, it can be 
 * associated with an existing object.
 * 
 * <p>Note that the dereferencing process can be rather cumbersome.  A 
 * typical event handler may look something like the following:
 * 
 * <p><code>
 * public void actionPerformed(ActionEvent ae) { <br>
 *     Type obj = (Type)((ExtendedJCheckBoxMenuItem)ae.getSource()).getReference();<br>
 *     ...<br>
 * }</code>
 * 
 * <p>Generics could be used to reduce the number of explicit casts, but at
 * the expense older JVMs.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 * 
 */
public class ExtendedJCheckBoxMenuItem extends JCheckBoxMenuItem
{
    private Object reference;

    public ExtendedJCheckBoxMenuItem(Object reference)
    {
        this.reference = reference;
    }

    public Object getReference()
    {
        return reference;
    }
}