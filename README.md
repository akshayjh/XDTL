# XDTL 2.0 

## What is XDTL

XDTL stands for eXtendable Data Transformation Language. It is an XML-based DSL providing basic tools for everyday data integration tasks.

## Installing

You will need Ant (http://ant.apache.org/bindownload.cgi) and JDK 1.8. All other required dependencies are included.

```
git clone git@github.com:mindworksindustries/XDTL xdtl
cd xdtl/org.mmx.xdtl
ant
```

Build creates all-including zip under build/ folder. Unpack it to desired location, create your first XDTL package file (see cookbook or test examples) and run it:

```
/path/to/xdtlrt.sh yourpackage.xdtl
```
