package com.digital.opuserp.view.home.apps.editor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class EditorView extends Window {


	String sourceCode = "package com.digital.opuserp;" +
						"class Test{" +
							"public static void main (String args[]){" +
								"System.out.println (\"Hello, dynamic compilation world!\");" +
							"}" +
						"}" ;
	
	public EditorView(){
		
		
		setContent(new Button("Testar",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				make();
				ClassLoader classLoader = EditorView.class.getClassLoader();
				try {
			        Class aClass = classLoader.loadClass("com.digital.opuserp.Test");
			        System.out.println("aClass.getName() = " + aClass.getName());
			    } catch (ClassNotFoundException e) {
			        e.printStackTrace();
			    }
			}
		}));
	}
	
	private void make(){
		SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject ("com.digital.opuserp.Test", sourceCode) ;
		JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
		
		System.out.println(System.getProperty("user.dir"));
		  
		
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
		
//		File f = new File(System.getProperty("user.dir")+File.separator+"bin");
//		f.c
		
		
		
		String[] compileOptions = new String[]{"-d", "/home/maconi/workspace_java/OpusERP4/src/com/digital/opuserp"} ;
		Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, compilationUnits) ;
		
		boolean status = compilerTask.call();
		
		if (!status){//If compilation error occurs
		    /*Iterate through each compilation problem and print it*/
		    for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
		        System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
		    }
		}
		
		try {
            stdFileManager.close() ;//Close the file manager
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
