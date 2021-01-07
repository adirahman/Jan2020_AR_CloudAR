package com.androiddevs.initialarsetup

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val url = "https://firebasestorage.googleapis.com/v0/b/arcore-try.appspot.com/o/allosaurus.glb?alt=media&token=f20260b0-4947-4058-8e42-a4d3c373f94b"
    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = fragment as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            spawnObject(hitResult.createAnchor(),Uri.parse(url))
        }
    }

    private fun spawnObject(anchor: Anchor,modelUri: Uri){
        val renderableSource = RenderableSource.builder()
            .setSource(this,modelUri,RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
            .build()
        ModelRenderable.builder()
            .setSource(this,renderableSource)
            .setRegistryId(modelUri)
            .build()
            .thenAccept {
                addNodeToScene(anchor,it)
            }.exceptionally {
                Log.e("MainActivity","Error Loading Model",it)
                null
            }
    }

    private fun addNodeToScene(anchor: Anchor, modelRenderable: ModelRenderable){
        val anchorNode =  AnchorNode(anchor)
        TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelRenderable
            setParent(anchorNode)
        }
        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}