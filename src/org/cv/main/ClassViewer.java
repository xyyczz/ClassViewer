package org.cv.main;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * class文件jar文件查看Java编译版本
 * @author zhaijz
 */
public class ClassViewer {
	public static final int MAGIC = 0xCAFEBABE;
	public static final int JAVA_MAJOR_BASE = 45;
	public Shell shell;
	public Label labelJavaVersion;
	public Label labelMainClass;
	public int minorVersion;
	public int majorVersion;
	public int javaVersion;
	public String clazz;
	
	public ClassViewer() {
		
	}
	
	public void init() {
		Display display = new Display();
		this.shell = new Shell(display);
		this.shell.setBounds(500, 500, 500, 500);
		this.shell.setLayout(new FillLayout());
		this.setShellScreenCenter();
		
		this.labelJavaVersion = new Label(shell, SWT.NONE);
		this.labelMainClass = new Label(shell, SWT.NONE);
		
		DropTarget dt1 = new DropTarget(shell, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY
				| DND.DROP_LINK);
		dt1.setTransfer(new Transfer[] {FileTransfer.getInstance()});
		dt1.addDropListener(new DropTargetAdapter(){
			public void dragEnter(DropTargetEvent event){
				
			}
			public void dropAccept(DropTargetEvent event){

			}
			public void drop(DropTargetEvent event){
				String[] paths = (String[]) event.data;
				if (paths.length > 0) {
					readClassInfo(paths[0]);
				}
			}
		});

		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}
	
	public void readClassInfo(String path) {
		try (DataInputStream dis = new DataInputStream(new FileInputStream(path))){
			int magic = dis.readInt();
			if (magic != MAGIC) {
				System.err.println("文件不是class文件");
				return;
			}
			readClassInfo(dis);
			
			showClassInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readClassInfo(DataInputStream dis) throws IOException {
		this.minorVersion = dis.readUnsignedShort();
		this.majorVersion = dis.readUnsignedShort();
		this.javaVersion = this.majorVersion - JAVA_MAJOR_BASE + 1;
		int constantPoolCount = dis.readUnsignedShort();
		dis.readByte();
		dis.readUnsignedShort();
		dis.readByte();
		String mainClass = dis.readUTF();
		this.clazz = mainClass.replace("/", ".");
	}
	
	private void showClassInfo() {
		this.labelJavaVersion.setText("JavaVersion: " + String.valueOf(this.javaVersion));
		this.labelMainClass.setText("Class: " + this.clazz);
	}
	
	private void setShellScreenCenter() {
		Rectangle clientArea = shell.getMonitor().getClientArea();
		Rectangle shellArea = shell.getClientArea();
		int x = clientArea.width / 2 - shellArea.width / 2;
		int y = clientArea.height / 2 - shellArea.height / 2;
		shell.setLocation(x, y);
	}
	
	public static void main(String[] args) {
		ClassViewer classViewer = new ClassViewer();
		classViewer.init();
	}
}
