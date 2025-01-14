package com.speakerboxlite.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

class RouteControllerVMProcessor(processingEnv: ProcessingEnvironment,
                                 kaptKotlinGeneratedDir: String,
                                 mainRouterPack: String): RouteControllerProcessorBase(processingEnv, kaptKotlinGeneratedDir, mainRouterPack)
{
    override fun createClass(element: TypeElement): ClassName
    {
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        val className = element.simpleName.toString()

        val elementClassName = ClassName(pack, className)
        val names = element.getExecutables().map { it.simpleName.toString() }
        if (names.containsAll(REQUIRED_METHODS))
            return elementClassName

        val classNameImpl = "${className}_IMP"
        val classBuilder = TypeSpec.classBuilder(classNameImpl)
        val typeArguments = (element.superclass as DeclaredType).typeArguments as List<DeclaredType>

        val pathElement = typeArguments[PATH_INDEX].asElement() as TypeElement
        val pathClass = ClassName(pathElement.getPack(processingEnv), pathElement.simpleName.toString())

        val viewElement = typeArguments[V_INDEX].asElement() as TypeElement
        val viewClass = ClassName(viewElement.getPack(processingEnv), viewElement.simpleName.toString())

        val vmElement = typeArguments[VM_INDEX].asElement() as TypeElement
        val vmClass = ClassName(vmElement.getPack(processingEnv), vmElement.simpleName.toString())

        if (!names.contains(CREATE_VIEW))
        {
            val func = FunSpec.builder(CREATE_VIEW)
            func.addModifiers(KModifier.OVERRIDE)
            func.addParameter("path", pathClass)
            func.returns(viewClass)
            func.addStatement("return %T()", viewClass)
            classBuilder.addFunction(func.build())
        }

        if (!names.contains(CREATE_VIEWMODEL))
        {
            val getAndroidViewModel = MemberName(mainRouterPack, "getAndroidViewModel")

            val func = FunSpec.builder(CREATE_VIEWMODEL)
            func.addModifiers(KModifier.OVERRIDE)
            func.addModifiers(KModifier.PROTECTED)
            func.addParameter("view", viewClass)
            func.addParameter("path", pathClass)
            func.returns(vmClass)
            func.addStatement("return view.%M()", getAndroidViewModel)
            classBuilder.addFunction(func.build())
        }

        classBuilder.superclass(elementClassName)

        val file = FileSpec.builder(pack, classNameImpl)
            .addType(classBuilder.build())
            .build()

        file.writeTo(File(kaptKotlinGeneratedDir))
        return ClassName(pack, classNameImpl)
    }

    companion object
    {
        val PATH_INDEX = 0
        val VM_INDEX = 1
        val V_INDEX = 2

        const val CREATE_VIEW = "onCreateView"
        const val CREATE_VIEWMODEL = "onCreateViewModel"

        val REQUIRED_METHODS = listOf(CREATE_VIEW, CREATE_VIEWMODEL)
    }
}