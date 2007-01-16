package edu.indiana.cs.eac.testing.ui;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class SWTTest {

public static void main (String [] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	shell.pack ();
	shell.open ();
	final boolean [] result = new boolean [1];
	final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	dialog.setLayout (new RowLayout ());
	final Button ok = new Button (dialog, SWT.PUSH);
	ok.setText ("OK");
	Button cancel = new Button (dialog, SWT.PUSH);
	cancel.setText ("Cancel");
	Listener listener =new Listener () {
		public void handleEvent (Event event) {
			result [0] = event.widget == ok;
			dialog.close ();
		}
	};
	ok.addListener (SWT.Selection, listener);
	cancel.addListener (SWT.Selection, listener);
	dialog.pack ();
	dialog.open ();
	System.out.println ("Prompt ...");
	while (!dialog.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	System.out.println ("Result: " + result [0]);
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 