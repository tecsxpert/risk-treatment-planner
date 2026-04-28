import os
import chromadb
from sentence_transformers import SentenceTransformer

# Initialize ChromaDB
chroma_client = chromadb.PersistentClient(path="./chroma_data")
collection = chroma_client.get_or_create_collection(name="risk_knowledge")

# Load sentence transformer model
model = SentenceTransformer('all-MiniLM-L6-v2')

def embed_text(text):
    return model.encode(text).tolist()

def add_document(doc_id, text, metadata={}):
    embedding = embed_text(text)
    collection.add(
        ids=[doc_id],
        embeddings=[embedding],
        documents=[text],
        metadatas=[metadata]
    )

def query_documents(query_text, n_results=3):
    embedding = embed_text(query_text)
    results = collection.query(
        query_embeddings=[embedding],
        n_results=n_results
    )
    return results