package ba.unsa.rpr.tutorijal7;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Tutorijal {
    public static void main(String[] args) {
        zapisiXml(ucitajXml(ucitajGradove()));
    }

    public static ArrayList<Grad> ucitajGradove() {
        ArrayList<Grad> ucitani = new ArrayList<Grad>();
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileReader("mjerenja.txt"));
            while (ulaz.hasNextLine()) {
                Grad novi = new Grad();
                String cijelaLinija = ulaz.nextLine();
                int i = 0;
                for (; i < cijelaLinija.length(); i++) {
                    if (cijelaLinija.charAt(i) == ',') {
                        novi.setNaziv(cijelaLinija.substring(0, i));
                        break;
                    }
                }
                i++;
                int pocetakBroja = -1;
                Double[] temperature = new Double[1000];
                for (int j = 0; j < novi.getTemperature().length && i < cijelaLinija.length(); j++) {
                    if (pocetakBroja == -1) pocetakBroja = i;
                    if (cijelaLinija.charAt(i) == ',') {
                        temperature[j] = Double.parseDouble(cijelaLinija.substring(pocetakBroja, i));
                        j++;
                        pocetakBroja = -1;
                    } else if (i == cijelaLinija.length() - 1) {
                        temperature[j] = Double.parseDouble(cijelaLinija.substring(pocetakBroja, i + 1));
                    }
                }
                novi.setTemperature(temperature);
                ucitani.add(novi);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Datoteka mjerenja.txt ne postoji");
        } finally {
            ulaz.close();
        }
        return ucitani;
    }


    public static UN ucitajXml(ArrayList<Grad> gradovi) {
        ArrayList<Drzava> ucitane = new ArrayList<Drzava>();
        Document xml = null;
        try {
            DocumentBuilder citac = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xml = citac.parse(new File("drzave.xml"));
            Element korijen = xml.getDocumentElement();
            NodeList djeca = korijen.getChildNodes();

            for (int i = 0; i < djeca.getLength(); i++) {
                Node drzava = djeca.item(i);
                if (drzava instanceof Element && drzava.getNodeName().equals("#text")) continue;
                if (drzava instanceof Element && ((Element) drzava).getTagName().equals("drzava")) {

                    int brojStanovnika = Integer.parseInt(((Element) drzava).getAttribute("stanovnika"));
                    String naziv = ((Element) drzava).getElementsByTagName("naziv").item(0).getTextContent();
                    Double povrsina = Double.parseDouble(((Element) drzava).getElementsByTagName("povrsina").item(0).getTextContent());
                    String jedinicaZaPovrsinu = ((Element) ((Element) drzava).getElementsByTagName("povrsina").item(0)).getAttribute("jedinica");
                    NodeList glavniGrad = ((Element) drzava).getElementsByTagName("glavnigrad");
                    String nazivGlavnogGrada = ((Element) glavniGrad.item(0)).getElementsByTagName("naziv").item(0).getTextContent();
                    int brojStanovnikaGlavnogGrada = Integer.parseInt(((Element) glavniGrad.item(0)).getAttribute("stanovnika"));
                    Double[] temperature = new Double[1000];
                    Grad postoji = null;
                    for (int j = 0; j < gradovi.size(); j++) {
                        if (gradovi.get(j).getNaziv().equals(nazivGlavnogGrada)) {
                            postoji = gradovi.get(j);
                            temperature = gradovi.get(j).getTemperature();
                            gradovi.get(j).setBrojStanovnika(brojStanovnikaGlavnogGrada);
                            break;
                        }
                    }
                    Drzava nova = new Drzava();
                    if(postoji != null) {
                        nova.setNaziv(naziv);
                        nova.setBrojStanovnika(brojStanovnika);
                        nova.setPovrsina(povrsina);
                        nova.setJedinicaZaPovrsinu(jedinicaZaPovrsinu);
                        nova.setGlavniGrad(postoji);
                        ucitane.add(nova);
                    }
                    else{
                        Grad nepostojeci = new Grad();
                        nepostojeci.setNaziv(nazivGlavnogGrada);
                        nepostojeci.setBrojStanovnika(brojStanovnikaGlavnogGrada);
                        nepostojeci.setTemperature(null);
                        nova.setGlavniGrad(nepostojeci);
                        nova.setNaziv(naziv);
                        nova.setBrojStanovnika(brojStanovnika);
                        nova.setPovrsina(povrsina);
                        nova.setJedinicaZaPovrsinu(jedinicaZaPovrsinu);
                        ucitane.add(nova);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("drzave.xml nije validan XML dokument");
        }
        UN un = new UN();
        un.setClanice(ucitane);
        return un;
    }

    public static void zapisiXml(UN un){
        XMLEncoder zapis = null;

        try{
            zapis = new XMLEncoder(new FileOutputStream("un.xml"));
            zapis.writeObject(un);
        } catch (FileNotFoundException e){
            System.out.println("Datoteka ne postoji!");
        } finally {
            zapis.close();
        }
    }
}
