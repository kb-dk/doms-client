package dk.statsbiblioteket.doms.client.util;



import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	
	private Util()
	{
		
	}

	public static String getInitParameter(String paramName) {
		return FacesContext.getCurrentInstance().getExternalContext().getInitParameter(paramName);
	}
	
	public static ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance()
                           .getExternalContext();
    }
	

	public static void addFacesMessage(String msg) {
		FacesMessages.instance().add(new FacesMessage(msg));
	}
	
    public static String getChecksum(File file) throws NoSuchAlgorithmException, IOException {
   	 InputStream fis =  new FileInputStream(file);
        byte[] buffer = new byte[1024];
        MessageDigest digest = MessageDigest.getInstance("MD5");
        int numRead;
        do {
         numRead = fis.read(buffer);
         if (numRead > 0) {
           digest.update(buffer, 0, numRead);
           }
         } while (numRead != -1);
        fis.close();
        return digest.digest().toString();

	}
	
	public static String getCurrentUserName()
	{
		return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
	}
	
	public static String getContentModelPath()
	{
		String filepath = "C:\\Tomcat6.0\\webapps\\domsgui\\protected\\data\\testbed2\\";
		if (FacesContext.getCurrentInstance()!=null)
		{
			filepath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("filepath");
	
			if (filepath == null) 
			{
				filepath = "";
			}
		    else
		    {
		    	if ( !(filepath.endsWith("\\") || filepath.endsWith("/")))
		    	{
		    		filepath += File.separatorChar;
		    	}
		     }
		}
		return filepath;
	}
	
	/**
     * Create ValueExpression object based on the given value expression string and value type.
     * This is to be used in UIComponent#setValueExpression().
     * @param valueExpression The value expression string, e.g. "#{myBean.someValue}".
     * @param valueType The actual value object type, e.g. String.class.
     * @return The ValueExpression object to be used in UIComponent#setValueExpression().
     */
    public static ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getExpressionFactory().createValueExpression(
            facesContext.getELContext(), valueExpression, valueType);
    }
    
    /**
     * Create MethodExpression object based on the given action expression string and return type.
     * This is to be used in UICommand#setActionExpression().
     * @param actionExpression The action expression string, e.g. "#{myBean.action}".
     * @param returnType The actual return type of the action, e.g. String.class or null (void).
     * @return The MethodExpression object to be used in UICommand#setActionExpression().
     */
    public static MethodExpression createActionExpression(String actionExpression, Class<?> returnType) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getExpressionFactory().createMethodExpression(
            facesContext.getELContext(), actionExpression, returnType, new Class[0]);
    }
    
    public static MethodExpressionActionListener createActionListnerMethod(String actionExpression)
    {
    	FacesContext context = FacesContext.getCurrentInstance();   
    	ELContext elContext = context .getELContext();   
    	MethodExpression actionListenerExpression = context.getApplication().getExpressionFactory().createMethodExpression(   
    	elContext,actionExpression,null,new Class[] {ActionEvent.class});   
    	  
    	 MethodExpressionActionListener methodExpressionActionListener = new MethodExpressionActionListener(actionListenerExpression);  

        return methodExpressionActionListener;
    }
    
    /**
     * Extract an integer from a string.
     * @param str The string from which to extract the integer.
     */
    public static int extractInt(String str)
    {
      int res = 0;
      if (str!=null)
      {
	      try {
	        res = Integer.parseInt(str);
	      }
	      catch (NumberFormatException numberFormatEx) {
	        res = 0;
	      }
      }
      return res;
    }
    
    /**
     * For test.
     * @param is
     */
    public static void dumpInputStream(InputStream is)
	{
		if (is!=null)
    	{
			try
			{
	    		System.out.println("\nDataStream fetched using fc.getDatastreamDissemination.\n");
	        	int i = is.read();
	        	char c;
	        	while (i!=-1)
	        	{
	        		c = (char)i;
	        		System.out.print(c);
	        		i = is.read();
	        	}
	        	is.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace(System.out);
			}
    	}
	}
}
