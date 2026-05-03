from flask import Blueprint, request, jsonify
from services.chroma_client import add_document, query_documents
from services.groq_client import call_groq
from datetime import datetime

query_bp = Blueprint('query', __name__)

@query_bp.route('/query', methods=['POST'])
def query():
    data = request.get_json()

    if not data or 'question' not in data or not data['question']:
        return jsonify({"error": "Missing required field: question"}), 400

    question = str(data['question'])[:500]

    try:
        # Retrieve top 3 relevant chunks from ChromaDB
        results = query_documents(question, n_results=3)
        
        # Build context from retrieved chunks
        context = ""
        sources = []
        if results and results['documents']:
            for i, doc in enumerate(results['documents'][0]):
                context += f"Source {i+1}: {doc}\n\n"
                sources.append(doc[:100])

        # Build prompt with context
        prompt = f"""You are a risk management expert.

Using the following context, answer the question professionally.

Context:
{context}

Question: {question}

Provide a clear, professional answer based on the context above."""

        answer = call_groq(prompt)

        return jsonify({
            "question": question,
            "answer": answer,
            "sources": sources,
            "generated_at": datetime.utcnow().isoformat() + 'Z'
        }), 200

    except Exception as e:
        return jsonify({
            "error": "RAG query error",
            "details": str(e),
            "is_fallback": True
        }), 500


@query_bp.route('/ingest', methods=['POST'])
def ingest():
    data = request.get_json()

    if not data or 'text' not in data or not data['text']:
        return jsonify({"error": "Missing required field: text"}), 400

    try:
        doc_id = f"doc_{datetime.utcnow().timestamp()}"
        add_document(
            doc_id=doc_id,
            text=data['text'],
            metadata={"source": data.get("source", "manual")}
        )
        return jsonify({
            "message": "Document ingested successfully",
            "doc_id": doc_id
        }), 200

    except Exception as e:
        return jsonify({
            "error": "Ingestion error",
            "details": str(e)
        }), 500