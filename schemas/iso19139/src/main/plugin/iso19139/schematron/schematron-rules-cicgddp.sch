<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <sch:title xmlns="http://www.w3.org/2001/XMLSchema">Schematron validation / CIC:GDDP recommendations</sch:title>
    <sch:ns prefix="gml" uri="http://www.opengis.net/gml"/>
    <sch:ns prefix="gmd" uri="http://www.isotc211.org/2005/gmd"/>
    <sch:ns prefix="srv" uri="http://www.isotc211.org/2005/srv"/>
    <sch:ns prefix="gco" uri="http://www.isotc211.org/2005/gco"/>
    <sch:ns prefix="geonet" uri="http://www.fao.org/geonetwork"/>
    <sch:ns prefix="xlink" uri="http://www.w3.org/1999/xlink"/>

    <!-- =============================================================
    GeoNetwork schematron rules:
    ============================================================= -->
    <sch:pattern>
        <sch:title>$loc/strings/M1</sch:title>
        <sch:rule
            context="//gmd:MD_Metadata|//*[@gco:isoType='gmd:MD_Metadata']">
        	<sch:let name="title" value="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/>

            <sch:assert test="$title and $title != ''">$loc/strings/alert.M1</sch:assert>
            <sch:report test="$title"><sch:value-of select="$loc/strings/report.M1"/> "<sch:value-of select="normalize-space($title)"/>"</sch:report>

        </sch:rule>
    </sch:pattern>
    <sch:pattern>
        <sch:title>$loc/strings/M2</sch:title>
        <sch:rule
            context="//gmd:MD_Metadata|//*[@gco:isoType='gmd:MD_Metadata']">
            <sch:let name="abstract" value="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString"/>

            <sch:assert test="$abstract and $abstract != ''">$loc/strings/alert.M2</sch:assert>
            <sch:report test="$abstract"><sch:value-of select="$loc/strings/report.M2"/> "<sch:value-of select="normalize-space($abstract)"/>"</sch:report>

        </sch:rule>
    </sch:pattern>
    <sch:pattern>
        <sch:title>$loc/strings/M3</sch:title>
        <sch:rule
            context="/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification">
            <sch:let name="topicCategory" value="gmd:topicCategory"/>

            <sch:assert test="count($topicCategory) > 0">$loc/strings/alert.M3</sch:assert>
            <sch:assert test="$topicCategory/gmd:MD_TopicCategoryCode and $topicCategory/gmd:MD_TopicCategoryCode != ''">$loc/strings/alert.M3</sch:assert>
            <sch:report test="$topicCategory"><sch:value-of select="$loc/strings/report.M3"/> "<sch:value-of select="$topicCategory"/>"</sch:report>

        </sch:rule>
    </sch:pattern>
    <sch:pattern>
        <sch:title>$loc/strings/M4</sch:title>
        <sch:rule
            context="/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation">
            <sch:let name="respParty" value="gmd:citedResponsibleParty"/>
            <sch:let name="role" value="$respParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='publisher']"/>
            <sch:let name="name" value="$respParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='publisher']/../../gmd:organisationName|$respParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='publisher']/../../gmd:individualName"/>
            
            <sch:assert test="$role">$loc/strings/alert.M41</sch:assert>
            <sch:assert test="$name and $name/gco:CharacterString != ''">$loc/strings/alert.M42</sch:assert>
            <sch:report test="$name"><sch:value-of select="$loc/strings/report.M4"/> "<sch:value-of select="$name"/>"</sch:report>
     
           
            

        </sch:rule>
    </sch:pattern>

    <sch:pattern>
        <sch:title>$loc/strings/M5</sch:title>
        <sch:rule
            context="/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation">
            <sch:let name="respParty" value="gmd:citedResponsibleParty"/>
            <sch:let name="role" value="$respParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='originator']"/>
            <sch:let name="name" value="$respParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='originator']/../../gmd:organisationName|$respParty/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='originator']/../../gmd:individualName"/>


            <sch:assert test="$role">$loc/strings/alert.M51</sch:assert>
            <sch:assert test="$name and $name/gco:CharacterString != ''">$loc/strings/alert.M52</sch:assert>
            <sch:report test="$name"><sch:value-of select="$loc/strings/report.M5"/> "<sch:value-of select="$name"/>"</sch:report>

        </sch:rule>
    </sch:pattern>

    <sch:pattern>
        <sch:title>$loc/strings/M6</sch:title>
        <sch:rule
            context="/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation">
            <sch:let name="date" value="gmd:date"/>

            <sch:assert test="count($date) > 0">$loc/strings/alert.M6</sch:assert>
            <sch:assert test="$date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode[@codeListValue='publication'] and $date/gmd:CI_Date/gmd:date/*/text() !=''">$loc/strings/alert.M6</sch:assert>
            <sch:report test="$date"><sch:value-of select="$loc/strings/report.M6"/> "<sch:value-of select="$date"/>"</sch:report>

        </sch:rule>
    </sch:pattern>
    <sch:pattern>
        <sch:title>$loc/strings/M7</sch:title>
        <sch:rule
            context="/gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution">
            <sch:let name="distFormat" value="gmd:distributionFormat"/>
            <sch:let name="formatname" value="$distFormat/gmd:MD_Format/gmd:name/gco:CharacterString"/>

            <sch:assert test="$distFormat">$loc/strings/alert.M7</sch:assert>
            <sch:assert test="$formatname and $formatname !=''">$loc/strings/alert.M7</sch:assert>
            <sch:report test="$formatname"><sch:value-of select="$loc/strings/report.M7"/> "<sch:value-of select="$formatname"/>"</sch:report>

        </sch:rule>
    </sch:pattern>


</sch:schema>
