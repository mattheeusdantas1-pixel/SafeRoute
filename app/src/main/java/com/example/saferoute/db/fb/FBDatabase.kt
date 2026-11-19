package com.example.saferoute.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onContatoAdded(contato: FBContato)
        fun onContatoRemoved(contato: FBContato)
        fun onTrajetoAdded(trajeto: FBTrajeto)
        fun onTrajetoUpdated(trajeto: FBTrajeto)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var userReg: ListenerRegistration? = null
    private var contatosReg: ListenerRegistration? = null
    private var trajetosReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            // Troca de sessão: para de ouvir o banco da pessoa anterior
            userReg?.remove()
            contatosReg?.remove()
            trajetosReg?.remove()
            if (auth.currentUser == null) {
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
            val refUsuario = db.collection("users").document(auth.currentUser!!.uid)

            // Ouve o documento (e não um get único), porque no cadastro ele pode ser gravado
            // um instante depois de a sessão abrir
            userReg = refUsuario.addSnapshotListener { snapshot, ex ->
                if (ex != null) return@addSnapshotListener
                snapshot?.toObject(FBUser::class.java)?.let { listener?.onUserLoaded(it) }
            }
            contatosReg = refUsuario.collection("contatos")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fb = change.document.toObject(FBContato::class.java)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> listener?.onContatoAdded(fb)
                            DocumentChange.Type.REMOVED -> listener?.onContatoRemoved(fb)
                            else -> {}
                        }
                    }
                }
            trajetosReg = refUsuario.collection("trajetos")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fb = change.document.toObject(FBTrajeto::class.java)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> listener?.onTrajetoAdded(fb)
                            DocumentChange.Type.MODIFIED -> listener?.onTrajetoUpdated(fb)
                            else -> {}
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    // Grava (ou substitui) o documento da pessoa: nome, e-mail, telefone e gesto de sacudir
    fun salvarUsuario(user: FBUser) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        db.collection("users").document(uid).set(user)
    }

    fun adicionarContato(contato: FBContato) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        if (contato.id.isNullOrEmpty()) throw RuntimeException("Contato sem id!")
        db.collection("users").document(uid).collection("contatos")
            .document(contato.id!!).set(contato)
    }

    fun removerContato(contato: FBContato) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        if (contato.id.isNullOrEmpty()) throw RuntimeException("Contato sem id!")
        db.collection("users").document(uid).collection("contatos")
            .document(contato.id!!).delete()
    }

    // Cada trajeto é um documento em users/{uid}/trajetos/{id}
    fun salvarTrajeto(trajeto: FBTrajeto) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        if (trajeto.id.isNullOrEmpty()) throw RuntimeException("Trajeto sem id!")
        db.collection("users").document(uid).collection("trajetos")
            .document(trajeto.id!!).set(trajeto)
    }

    companion object {
        // Usado no cadastro, antes de existir um ViewModel. Não cria ouvintes.
        fun registrarUsuario(user: FBUser) {
            val uid = Firebase.auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
            Firebase.firestore.collection("users").document(uid).set(user)
        }
    }
}
