<?xml version="1.0"?>
<xsl:stylesheet version="3.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:hd="urn:header">

    <xsl:param name="csv" select="'one.csv'"/>
    <xsl:param name="sep" select="','"/>
    <xsl:param name="rootElement" select="'root'"/>
    <xsl:param name="rowElement" select="'row'"/>
    <xsl:param name="firstRow" select="true()"/>

    <xsl:variable name="header" select="tokenize(unparsed-text-lines($csv)[1], $sep)"/>

    <xsl:function name="hd:header" as="xs:string">
        <xsl:param name="col"/>
        <xsl:choose>
            <xsl:when test="$firstRow">
                <xsl:value-of select="$header[$col]"/>
            </xsl:when>
            <xsl:otherwise>item</xsl:otherwise>
    </xsl:choose>
    </xsl:function>

    <xsl:template match="/">
        <xsl:element name="{$rootElement}">
            <xsl:for-each select="unparsed-text-lines($csv)[position() &gt; 1]">
                <xsl:element name="{$rowElement}">
                    <xsl:for-each select="tokenize(., $sep)">
                        <xsl:variable name="pos" select="position()"/>
                        <xsl:element name="{hd:header($pos)}">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
