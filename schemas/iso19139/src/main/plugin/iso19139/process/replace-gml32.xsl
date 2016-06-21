<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:gco="http://www.isotc211.org/2005/gco"
    xmlns:gmd="http://www.isotc211.org/2005/gmd"
    xmlns:gmi="http://www.isotc211.org/2005/gmi"
    xmlns:gml="http://www.opengis.net/gml"
    xmlns:gml32="http://www.opengis.net/gml/3.2"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:geonet="http://www.fao.org/geonetwork"
    version="2.0" exclude-result-prefixes="gml gmd gco gmi xsl xs">
    <xsl:output version="1.0" omit-xml-declaration="yes" indent="no"/>
    <xsl:strip-space elements="*"/>

  
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
    <!-- Remove geonet:* elements. -->
    <xsl:template match="geonet:*" priority="2"/>
    <xsl:template match="gml32:*"/>
    



</xsl:stylesheet>
