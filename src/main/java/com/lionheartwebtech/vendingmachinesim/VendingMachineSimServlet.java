package com.lionheartwebtech.vendingmachinesim;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import freemarker.core.ParseException;
import freemarker.template.*;

import org.apache.log4j.Logger;


public class VendingMachineSimServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(VendingMachineSimServlet.class.getName());
    
    private static Connection jdbcConnection = null;
    private static Configuration fmConfig = new Configuration(Configuration.getVersion());
    private static final String TEMPLATE_DIR = "/WEB-INF/templates";
    
    @Override
    public void init(ServletConfig config) throws UnavailableException {
        logger.info("==============================");
        logger.info("Starting " + VendingMachineSimServlet.class.getSimpleName() + " servlet init");
        logger.info("==============================");
        
        logger.info("Getting real path for templateDir");
        String templateDir = config.getServletContext().getRealPath(TEMPLATE_DIR);
        logger.info("...real path is: " + templateDir);
        
        logger.info("Initializing Freemarker, templateDir: " + templateDir);
        try {
            fmConfig.setDirectoryForTemplateLoading(new File(templateDir));
            logger.info("Successfully Loaded Freemarker");
        } catch (IOException e) {
            logger.error("Template directory not found, directory: " + templateDir + ", exception: " + e);
        }
        
            logger.info("Connecting to the database...");
        
        String jdbcDriver = "org.mariadb.jdbc.Driver";
        logger.info("Loading JDBC Driver: " + jdbcDriver);
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to find JDBC driver on classpath.");
            return;
        }
        //Creating new connection
        String connString = "jdbc:mariadb://";
        connString += "lionheartwebtech-db.cv18zcsjzteu.us-west-2.rds.amazonaws.com:3306";
        connString += "/vashon";
        connString += "?user=vashon&password=vashon";
        connString += "&useSSL=true&trustServerCertificate=true";
        
    try {
            jdbcConnection = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            logger.error("Unable to connect to SQL Database with JDBC string: " + connString);
            throw new UnavailableException("Unable to connect to database.");
        }
        
        logger.info("...connected!");
        
        logger.info("==============================");
        logger.info("Finished init");
        logger.info("==============================");
    }
    
    @Override
    public void destroy() {
        logger.info("##############################");
        logger.info("Destroying " + VendingMachineSimServlet.class.getSimpleName() + " servlet");
        logger.info("##############################");

        logger.info("Disconnecting from the database.");
        try {
            jdbcConnection.close();
        } catch (SQLException e) {
            logger.error("Exception thrown while trying to close SQL Connection: " + e, e);
        }
        logger.info("Disconneced!");
        
        logger.info("##############################");
        logger.info("...done");
        logger.info("##############################");
    }
    
    //TODO: doGet() 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long timeStart = System.currentTimeMillis();
        logger.debug("IN - doGet()");
               
        String command = request.getParameter("cmd");
        if (command == null) command = "home";

        String template = "";
        Map<String, Object> model = new HashMap<>();
        //TODO: Switch for templates
        switch (command) {
            case "home":
                template = "homepage.tpl";
                break;
                
            default:
                logger.info("Invalid GET command received: " + command);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        processTemplate(response, template, model);
        long time = System.currentTimeMillis() - timeStart;
        logger.info("OUT - doGet() - " + time + "ms");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long timeStart = System.currentTimeMillis();
        logger.debug("IN - doPost()");

        String command = request.getParameter("cmd");
        if (command == null) {
            logger.info("No cmd parameter received");
            command = "";
        }

        String template = "";
        Map<String, Object> model = new HashMap<>();

        switch (command) {
            case "example":
                // Example command code goes here
                break;
                
            default:
                logger.info("Invalid POST command received: " + command);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        processTemplate(response, template, model);
        long time = System.currentTimeMillis() - timeStart;
        logger.debug("OUT - doPost() - " + time + "ms");
    }        


private void processTemplate(HttpServletResponse response, String template, Map<String, Object> model) {
        logger.debug("Processing Template: " + template);
        
        try (PrintWriter out = response.getWriter()) {
            Template view = fmConfig.getTemplate(template);
            view.process(model, out);
        } catch (TemplateException e) {
            logger.error("Template Error:", e);
        } catch (MalformedTemplateNameException e) {
            logger.error("Malformed Template Error:", e);
        } catch (ParseException e) {
            logger.error("Parsing Error:", e);
        } catch (IOException e) {
            logger.error("IO Error:", e);
        } 
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
