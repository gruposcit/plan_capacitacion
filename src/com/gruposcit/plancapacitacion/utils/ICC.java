/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposcit.plancapacitacion.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.aguilar.swinglib.utils.EasyEntry;
import org.aguilar.swinglib.utils.EasyMap;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author SISTEMAS
 */
public class ICC {
    
    private static final String NODO_RAIZ = "informe";
    public static final String EXTENSION = ".icc";
    private static final String XMLNS = "http://www.cnbv.gob.mx/recepcion/icc";
    private static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_SCHEMALOCATION = "http://www.cnbv.gob.mx/recepcion/icc icc.xsd";
     
    
    public Modelo importar(String rutaArchivo) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(rutaArchivo));
        doc.getDocumentElement().normalize();
        ICC.Modelo modelo = new ICC.Modelo();
        modelo.setOrganoSupervisor(doc.getElementsByTagName("clave_organo_regulador").item(0).getTextContent());
        modelo.setSujetoObligado(doc.getElementsByTagName("clave_sujeto_obligado").item(0).getTextContent());
        modelo.setPeriodoReportar(doc.getElementsByTagName("periodo_informado").item(0).getTextContent());
        NodeList actual = doc.getElementsByTagName("cursos_programados");
        NodeList anterior = doc.getElementsByTagName("cursos_efectuados");
        ArrayList<Map> alActual = new ArrayList<>();
        ArrayList<Map> alAnterior = new ArrayList<>();
        for (int i = 0; i < actual.getLength(); i ++) {
            Node nodo = actual.item(i);
            NodeList hijos = nodo.getChildNodes();
            EasyEntry tipo = null, nombre = null, feini = null, fefin = null, areas = null, numper = null;
            for (int j = 0; j < hijos.getLength(); j ++) {
                if (hijos.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    switch (hijos.item(j).getNodeName()) {
                        case "tipo_capacitacion":
                            tipo = new EasyEntry("tipo", hijos.item(j).getTextContent()); break;
                        case "nombre_capacitacion":
                            nombre = new EasyEntry("nombre", hijos.item(j).getTextContent()); break;
                        case "fecha_inicio_imparticion":
                            feini = new EasyEntry("feini", hijos.item(j).getTextContent()); break;
                        case "fecha_fin_imparticion":
                            fefin = new EasyEntry("fefin", hijos.item(j).getTextContent()); break;
                        case "areas_capacitacion":
                            areas = new EasyEntry("areas", hijos.item(j).getTextContent()); break;
                        case "total_personas":
                            numper = new EasyEntry("num_per", hijos.item(j).getTextContent()); break;
                    }
                }
            }
            alActual.add(EasyMap.crearMap(tipo, nombre, feini, fefin, areas, numper));
        }
        modelo.setActual(alActual);
        for (int i = 0; i < anterior.getLength(); i ++) {
            Node nodo = anterior.item(i);
            NodeList hijos = nodo.getChildNodes();
            EasyEntry tipo = null, nombre = null, feini = null, fefin = null, areas = null, numper = null, documento = null;
            for (int j = 0; j < hijos.getLength(); j ++) {
                if (hijos.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    switch (hijos.item(j).getNodeName()) {
                        case "tipo_capacitacion":
                            tipo = new EasyEntry("tipo", hijos.item(j).getTextContent()); break;
                        case "nombre_capacitacion":
                            nombre = new EasyEntry("nombre", hijos.item(j).getTextContent()); break;
                        case "fecha_inicio_imparticion":
                            feini = new EasyEntry("feini", hijos.item(j).getTextContent()); break;
                        case "fecha_fin_imparticion":
                            fefin = new EasyEntry("fefin", hijos.item(j).getTextContent()); break;
                        case "areas_capacitadas":
                            areas = new EasyEntry("areas", hijos.item(j).getTextContent()); break;
                        case "total_personas_capacitadas":
                            numper = new EasyEntry("num_per", hijos.item(j).getTextContent()); break;
                        case "documento_emitido":
                            documento = new EasyEntry("documento", hijos.item(j).getTextContent()); break;
                    }
                }
            }
            alAnterior.add(EasyMap.crearMap(tipo, nombre, feini, fefin, areas, numper, documento));
        }
        modelo.setAnterior(alAnterior);
        return modelo;
    }
    public void exportar(String rutaArchivo, ICC.Modelo modelo) throws ParserConfigurationException, TransformerException {
        exportar(rutaArchivo, modelo.getOrganoSupervisor(), modelo.getSujetoObligado(), modelo.getPeriodoReportar(), modelo.actual, modelo.anterior);
    }
    public void exportar(String rutaArchivo, String organoSupervisor, String sujetoObligado, String periodoReportar, ArrayList<Map> actual, ArrayList<Map> anterior) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementation = builder.getDOMImplementation();
        Document document = implementation.createDocument(XMLNS, NODO_RAIZ, null);
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);

        Element informe = document.getDocumentElement();
        informe.setAttribute("xmlns:xsi",XMLNS_XSI);
        informe.setAttribute("xsi:schemaLocation", XSI_SCHEMALOCATION);

        Element organo = document.createElement("clave_organo_regulador");
        organo.setTextContent(organoSupervisor);
        informe.appendChild(organo);

        Element sujeto = document.createElement("clave_sujeto_obligado");
        sujeto.setTextContent(sujetoObligado);
        informe.appendChild(sujeto);

        Element periodo = document.createElement("periodo_informado");
        periodo.setTextContent(periodoReportar);
        informe.appendChild(periodo);

        Element datos_capacitacion = document.createElement("datos_capacitacion");
        informe.appendChild(datos_capacitacion);

        Element programa_anual = document.createElement("programa_anual");
        datos_capacitacion.appendChild(programa_anual);


        Element capacitacion_ano_anterior = document.createElement("capacitacion_ano_anterior");
        datos_capacitacion.appendChild(capacitacion_ano_anterior);
     
        for (Map dato : actual){
         
            Element cursos_programados = document.createElement("cursos_programados");
            Element tipo_capacitacion = document.createElement("tipo_capacitacion");
            Element nombre_capacitacion = document.createElement("nombre_capacitacion");
            Element fecha_inicio_imparticion = document.createElement("fecha_inicio_imparticion");
            Element fecha_fin_imparticion = document.createElement("fecha_fin_imparticion");
            Element areas_capacitacion = document.createElement("areas_capacitacion");
            Element total_personas = document.createElement("total_personas");

            tipo_capacitacion.setTextContent(dato.get("tipo").toString());
            cursos_programados.appendChild(tipo_capacitacion);

            nombre_capacitacion.setTextContent(dato.get("nombre").toString().toUpperCase());
            cursos_programados.appendChild(nombre_capacitacion);

            fecha_inicio_imparticion.setTextContent(dato.get("feini").toString());
            cursos_programados.appendChild(fecha_inicio_imparticion);

            fecha_fin_imparticion.setTextContent(dato.get("fefin").toString());
            cursos_programados.appendChild(fecha_fin_imparticion);  

            areas_capacitacion.setTextContent(dato.get("areas").toString());
            cursos_programados.appendChild(areas_capacitacion);

            total_personas.setTextContent(dato.get("num_per").toString());
            cursos_programados.appendChild(total_personas);

            programa_anual.appendChild(cursos_programados); 
            datos_capacitacion.appendChild(programa_anual); 
        }
     
        for (Map dato : anterior){
            Element cursos_programados = document.createElement("cursos_efectuados");
            Element tipo_capacitacion = document.createElement("tipo_capacitacion");
            Element nombre_capacitacion = document.createElement("nombre_capacitacion");
            Element fecha_inicio_imparticion = document.createElement("fecha_inicio_imparticion");
            Element fecha_fin_imparticion = document.createElement("fecha_fin_imparticion");
            Element areas_capacitacion = document.createElement("areas_capacitadas");
            Element total_personas = document.createElement("total_personas_capacitadas");
            Element documento_emitido = document.createElement("documento_emitido");

            tipo_capacitacion.setTextContent(dato.get("tipo").toString());
            cursos_programados.appendChild(tipo_capacitacion);

            nombre_capacitacion.setTextContent(dato.get("nombre").toString().toUpperCase());
            cursos_programados.appendChild(nombre_capacitacion);

            fecha_inicio_imparticion.setTextContent(dato.get("feini").toString());
            cursos_programados.appendChild(fecha_inicio_imparticion);

            fecha_fin_imparticion.setTextContent(dato.get("fefin").toString());
            cursos_programados.appendChild(fecha_fin_imparticion);  

            areas_capacitacion.setTextContent(dato.get("areas").toString());
            cursos_programados.appendChild(areas_capacitacion);

            total_personas.setTextContent(dato.get("num_per").toString());
            cursos_programados.appendChild(total_personas);

            documento_emitido.setTextContent(dato.get("documento").toString().toUpperCase());
            cursos_programados.appendChild(documento_emitido);


            capacitacion_ano_anterior.appendChild(cursos_programados); 
            datos_capacitacion.appendChild(capacitacion_ano_anterior); 
        }
        Source source = new DOMSource(document);
        Result result = new StreamResult(new java.io.File(rutaArchivo + EXTENSION));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(source, result);
    }
    public static boolean validarXMLSchema(String xsdPath, String xmlPath){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
    public static String obtenerRutaAplicacion() {
        final Class<?> referenceClass = ICC.class;
        final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
        try {
            final java.io.File jarPath = new java.io.File(url.toURI()).getParentFile();
            System.out.println(jarPath.getCanonicalPath());
            return jarPath.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(ICC.class.getName()).log(Level.SEVERE, null, ex);
        } catch(URISyntaxException ex){
            
        }
        return "";
    }
    public class Modelo {
        String organoSupervisor;
        String sujetoObligado;
        String periodoReportar;
        ArrayList<Map> actual;
        ArrayList<Map> anterior;
        
        public Modelo() {
            
        }
        public Modelo(String organoSupervisor, String sujetoObligado, String periodoReportar, ArrayList<Map> actual, ArrayList<Map> anterior) {
            this.organoSupervisor = organoSupervisor;
            this.sujetoObligado = sujetoObligado;
            this.periodoReportar = periodoReportar;
            this.actual = actual;
            this.anterior = anterior;
        }
        public String getOrganoSupervisor() {
            return organoSupervisor;
        }
        public void setOrganoSupervisor(String organoSupervisor) {
            this.organoSupervisor = organoSupervisor;
        }
        public String getSujetoObligado() {
            return sujetoObligado;
        }
        public void setSujetoObligado(String sujetoObligado) {
            this.sujetoObligado = sujetoObligado;
        }
        public String getPeriodoReportar() {
            return periodoReportar;
        }
        public void setPeriodoReportar(String periodoReportar) {
            this.periodoReportar = periodoReportar;
        }
        public ArrayList<Map> getActual() {
            return actual;
        }
        public void setActual(ArrayList<Map> actual) {
            this.actual = actual;
        }
        public ArrayList<Map> getAnterior() {
            return anterior;
        }
        public void setAnterior(ArrayList<Map> anterior) {
            this.anterior = anterior;
        }
    }
    
    public static void main(String[] args) throws ParserConfigurationException, TransformerException{
        ArrayList<Map> actuales = new ArrayList<>();
        ArrayList<Map> anteriores = new ArrayList<>();
        Map reg;
        
        for (int x = 0; x<3; x++){
            reg = new HashMap();
            reg.put("tipo", "0" +x);
            reg.put("nombre", "CAPACITATE HOY o que  " + x);
            reg.put("feini", "2016-06");
            reg.put("fefin", "2016-06");
            reg.put("areas", "SISTEMAS");
            reg.put("num_per", "12" + x);
            actuales.add(reg);
        }
        
         for (int x = 0; x<3; x++){
            reg = new HashMap();
            reg.put("tipo", "0" +x);
            reg.put("nombre", "YA PASO LA CAPACITACION AYER o que  " + x);
            reg.put("feini", "2016-06");
            reg.put("fefin", "2016-06");
            reg.put("areas", "SISTEMAS");
            reg.put("num_per", "12" + x);
            reg.put("documento", "CERTIFICADO");
            anteriores.add(reg);
        }
        System.out.println(actuales.size());
        System.out.println(actuales);
//        ICC.exportar("informe", new ICC.Modelo("01-002","89-123","2015", actuales, anteriores));
    }
    
}
