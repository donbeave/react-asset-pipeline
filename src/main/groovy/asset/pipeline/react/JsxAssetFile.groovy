package asset.pipeline.react

import asset.pipeline.AbstractAssetFile
import asset.pipeline.Processor
import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * An {@link asset.pipeline.AssetFile} implementation for JSX
 */
@CompileStatic
class JsxAssetFile extends AbstractAssetFile {
    static final List<String> contentType = ['text/jsx', 'application/javascript', 'application/x-javascript', 'text/javascript']
    static List<String> extensions = ['jsx']
    static String compiledExtension = 'js'
    static List<Class<Processor>> processors = [ReactProcessor]
    Pattern directivePattern = ~/(?m)^\/\/=(.*)/
}
