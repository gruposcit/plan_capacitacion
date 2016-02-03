/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposcit.plancapacitacion.utils;

import java.util.ArrayList;
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
    public GeneraXml(String nom_arch, String cad_organo, String cad_sujeto, String cad_periodo) throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
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
     
     
//    System.out.println(document.getTextContent());
    Source source = new DOMSource(document);
    Result result = new StreamResult(new java.io.File(nom_arch+".xml"));
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
     transformer.transform(source, result);
    }
    
    public static void main(String[] args) throws ParserConfigurationException, TransformerException{
        GeneraXml gen = new GeneraXml("informe","001-002","89-123","2015");
    }
    
}
