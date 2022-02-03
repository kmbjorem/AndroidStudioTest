package com.example.xml_api

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

// We don't use namespaces
private val ns: String? = null

class XmlParser {


    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<*> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }


    // Processes title tags in the feed.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Dog> {
        val dogList = mutableListOf<Dog>()

        // finds start tag, gos til it finds end tag.
        parser.require(XmlPullParser.START_TAG, ns, "dogs")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "dog") {
                dogList.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return dogList
    }

    // Parses the contents of an "entry". If it encounters a tag we want, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): Dog {
        parser.require(XmlPullParser.START_TAG, ns, "dog")
        var name: String? = null
        var age: Int? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "name" -> name = readAttribute(parser,parser.name)
                "age" -> age = readAttribute(parser,parser.name).toIntOrNull()
                else -> skip(parser)
            }
        }
        return Dog(name,age)
    }

    //Read method, Processes tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAttribute(parser: XmlPullParser, tag:String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val attribute = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return attribute
    }

    // For the tags, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    // Skips tags we don't want to read.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

}

data class Dog(val name:String?, val age:Int?)