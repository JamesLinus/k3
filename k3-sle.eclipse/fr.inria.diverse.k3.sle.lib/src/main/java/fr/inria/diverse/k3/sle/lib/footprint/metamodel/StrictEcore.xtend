package fr.inria.diverse.k3.sle.lib.footprint.metamodel

import java.util.ArrayList
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject

import static extension fr.inria.diverse.k3.sle.lib.footprint.metamodel.__SlicerAspect__.*

class StrictEcore{
	val List<EClass> inputEClass
	private val List<EObject> clonedElts = new ArrayList

	new(List<EClass> inputEClass){
		this.inputEClass = inputEClass
	}

	def void slice(){
		inputEClass?.forEach[visitToAddClasses(this)]
		inputEClass?.forEach[visitToAddRelations(this)]
//		save
	}

	def void objectCloned(EObject object){
		this.clonedElts.add(object)
	}

//	def void save(){
//		val objs = this.clonedElts.filter[eContainer==null]
//		val resSet = new ResourceSetImpl
//		resSet.getResourceFactoryRegistry.getExtensionToFactoryMap.put("*", new XMIResourceFactoryImpl)
//		val res = resSet.createResource(URI.createURI("modelSlice.xmi"))
//		res.getContents.addAll(objs)
//	    res.save(Collections.emptyMap)
//	    res.unload
//	}
}
