/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposcit.plancapacitacion.utils;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author SISTEMAS
 */
public class GeneraXml {
    String nom_arch;
//    public GeneraXml(String nom_arch){
//        this.nom_arch = nom_arch;
//    }
    
//    public GeneraXml(String nom_arch, ArrayList<Map> datos) throws ParserConfigurationException{
    public GeneraXml(String nom_arch, String cad_organo, String cad_sujeto, String cad_periodo, ArrayList<Map> actual) throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
//     if (datos.isEmpty()){
//         System.out.println("no hay datos para generar el archivo");
//         return;
//     }
     
     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     DocumentBuilder builder = factory.newDocumentBuilder();
     DOMImplementation implementation = builder.getDOMImplementation();
     Document document = implementation.createDocument("http://www.cnbv.gob.mx/recepcion/icc", nom_arch, null);
     document.setXmlVersion("1.0");
     document.setXmlStandalone(true);
     factory.setNamespaceAware(true);
     
     Element informe = document.getDocumentElement();
     
     Attr xmlns_xsi = document.createAttribute("xmlns:xsi");
     xmlns_xsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
     informe.setAttributeNode(xmlns_xsi);
          
     Attr xsi_schemaLocation = document.createAttribute("xsi:schemaLocation");
     xsi_schemaLocation.setValue("http://www.cnbv.gob.mx/recepcion/icc icc.xsd");
     informe.setAttributeNode(xsi_schemaLocation);
     
     Element organo = document.createElement("clave_organo_regulador");
     organo.setTextContent(cad_organo);
     informe.appendChild(organo);
     
     Element sujeto = document.createElement("clave_sujeto_obligado");
     sujeto.setTextContent(cad_sujeto);
     informe.appendChild(sujeto);
     
     Element periodo = document.createElement("periodo_informado");
     periodo.setTextContent(cad_periodo);
     informe.appendChild(periodo);
     
     Element datos_capacitacion = document.createElement("datos_capacitacion");
     informe.appendChild(datos_capacitacion);
     
     
     Element programa_anual = document.createElement("programa_anual");
     datos_capacitacion.appendChild(programa_anual);
     
     
     Element capacitacion_ano_anterior = document.createElement("capacitacion_ano_anterior");
     datos_capacitacion.appendChild(capacitacion_ano_anterior);
     
     Element cursos_programados = document.createElement("cursos_programados");
     
     
    Element tipo_capacitacion = document.createElement("tipo_capacitacion");
    Element nombre_capacitacion = document.createElement("nombre_capacitacion");
    Element fecha_inicio_imparticion = document.createElement("fecha_inicio_imparticion");
    Element fecha_fin_imparticion = document.createElement("fecha_fin_imparticion");
    Element areas_capacitacion = document.createElement("areas_capacitacion");
    Element total_personas = document.createElement("total_personas");

     
     for (Map dato : actual){
         System.out.println(dato);
        tipo_capacitacion.setTextContent(dato.get("tipo").toString());
        cursos_programados.appendChild(tipo_capacitacion);
              
        nombre_capacitacion.setTextContent(dato.get("nombre").toString());
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
     
     
    Source source = new DOMSource(document);
    Result result = new StreamResult(new java.io.File(nom_arch+".xml"));
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.transform(source, result);
    
    }
    
    public static void main(String[] args) throws ParserConfigurationException, TransformerException{
        ArrayList<Map> actuales = new ArrayList<Map>();
        Map reg = reg = new HashMap();
        
        reg.put("tipo", "01");
        reg.put("nombre", "CAPACITATE HOY o que");
        reg.put("feini", "2016-06");
        reg.put("fefin", "2016-06");
        reg.put("areas", "SISTEMAS");
        reg.put("num_per", "12");
        actuales.add(reg);
        
        Map reg2 = new HashMap();
        reg2.put("tipo", "01");
        reg2.put("nombre", "CAPACITATE HOY sale");
        reg2.put("feini", "2016-06");
        reg2.put("fefin", "2016-06");
        reg2.put("areas", "SISTEMAS,CONTABILIDAD");
        reg2.put("num_per", "12");
        actuales.add(reg2);
        
        
        System.out.println(actuales.size());
        System.out.println(actuales);
        
        GeneraXml gen = new GeneraXml("informe","001-002","89-123","2015", actuales );
    }
    
}
