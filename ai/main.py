from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util
import logging
from typing import List, Optional
# 로그 설정
logging.basicConfig(level=logging.INFO)

app = FastAPI()
model_save_path = "kr_sbert_finetuned"
loaded_model = SentenceTransformer(model_save_path)

class Sentences(BaseModel):
    sentence1: str
    sentence2: str

@app.post("/similarity")
def calculate_similarity(sentences: Sentences):
    try:
        embedding1 = loaded_model.encode(sentences.sentence1, convert_to_tensor=True)
        embedding2 = loaded_model.encode(sentences.sentence2, convert_to_tensor=True)
        similarity_score = util.cos_sim(embedding1, embedding2).item()
        return {"similarity_score": similarity_score}
    except Exception as e:
        logging.error(f"Error calculating similarity: {str(e)}")
        raise HTTPException(status_code=500, detail="Error calculating similarity")

# uvicorn main:app --host 0.0.0.0 --port 8000 --reload