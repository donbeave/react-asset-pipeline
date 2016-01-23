package asset.pipeline.react

import asset.pipeline.AbstractProcessor
import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import groovy.util.logging.Commons
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

@Commons
class ReactProcessor extends AbstractProcessor {

    public static final ThreadLocal threadLocal = new ThreadLocal()

    Scriptable globalScope
    ClassLoader classLoader

    ReactProcessor(AssetCompiler precompiler) {
        super(precompiler)
        classLoader = this.class.classLoader

        def shellJsResource = classLoader.getResource('asset/pipeline/react/shell.js')
        def envRhinoJsResource = classLoader.getResource('asset/pipeline/react/env.rhino.js')
        def jsxTransformerResource = classLoader.getResource('asset/pipeline/react/JSXTransformer.js')
        Context cx = Context.enter()

        cx.setOptimizationLevel(-1)
        globalScope = cx.initStandardObjects()
        cx.evaluateString(globalScope, shellJsResource.getText('UTF-8'), shellJsResource.file, 1, null)
        cx.evaluateString(globalScope, envRhinoJsResource.getText('UTF-8'), envRhinoJsResource.file, 1, null)
        cx.evaluateString(globalScope, jsxTransformerResource.getText('UTF-8'), jsxTransformerResource.file, 1, null)
        log.info('initilized')
    }

    String process(String input, AssetFile assetFile) {
        log.info("prefixing $assetFile.name")
        try {
            threadLocal.set(assetFile);

            def cx = Context.enter()
            def compileScope = cx.newObject(globalScope)
            compileScope.setParentScope(globalScope)
            compileScope.put('jsxSrc', compileScope, input)
            def result = cx.evaluateString(compileScope, 'JSXTransformer.transform(jsxSrc, {harmony: true}).code', 'jsx command', 0, null)
            return result.toString()
        } catch (Exception e) {
            throw new Exception("jsx-transforming $assetFile.name failed: $e")
        } finally {
            Context.exit()
        }
    }

    // used in shell.js
    static void print(text) {
        log.debug text
    }

}
