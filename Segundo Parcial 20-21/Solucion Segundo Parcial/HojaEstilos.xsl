<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:res="http://www.piat.dte.upm.es/ExamenJunio">


	<xsl:output method="html" encoding="UTF-8" indent="yes" />

	<xsl:key name="publisherKey" match="//res:publisher" use="."/>


	<xsl:template match="/">

		<html xmlns:res="http://www.piat.dte.upm.es/ExamenJunio">
			<head>
				<META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<title>Examen</title>
			</head>
			<body>
						<h1>Resultado</h1>
						<blockquote><!-- Obtener las urls de los publisher -->
							<xsl:for-each select="//res:publisher[generate-id()=generate-id(key('publisherKey',.))]">
								<xsl:value-of select="position()"/> - <xsl:element name="a" ><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></xsl:element> 
									<p>Centros asociados:</p>
									<blockquote>
										<xsl:variable name="publisherID" select="."/>
										<xsl:for-each select="//res:resource[res:publisher=string($publisherID)]">
											<xsl:value-of select="position()"/> - <xsl:value-of select="res:title"/> 
											<br/>
										</xsl:for-each>
									</blockquote>
							</xsl:for-each>
						</blockquote>
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>  
