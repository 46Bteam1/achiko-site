---
tags:
- sentence-transformers
- sentence-similarity
- feature-extraction
- generated_from_trainer
- dataset_size:5371
- loss:CosineSimilarityLoss
base_model: snunlp/KR-SBERT-V40K-klueNLI-augSTS
widget:
- source_sentence: 3층에는 침대와 다다미방과 화장실이 있다.
  sentences:
  - 태국 요리 교실에 처음 오신 분들께 추천합니다.
  - 세 팀이 2층에 한 개씩 두 층을 나눠 쓰는 것 같았어요.
  - 3층에는 침대, 다다미 방, 화장실이 있습니다.
- source_sentence: 에어비앤비이나, 관리업체가 따로있는거같아요.
  sentences:
  - 숙박시설에 접근하여 안전하게 이용할 수 있습니다.
  - 관리 업체가 영어는 어느정도 가능한 것 같아요.
  - 100일간의 유럽 여행 중 단연 최고의 숙소였습니다!
- source_sentence: 침대 매트에서 쉰내가 조금 나더라구요.
  sentences:
  - 침대 매트에서는 약간 쉰 냄새가 났어요.
  - 숙소, 침구, 발코니 등은 전부 마음에 들었습니다.
  - 콤콤한냄새가 나는건 어쩔수없는거 같아서 예민하신분은 피하세요.
- source_sentence: 2개의 테라스에서 감상하실수 있습니다.
  sentences:
  - 사진과 같이 테라스에서는 멋있는 뷰를 감상하실 수 있습니다.
  - 청결도 위치 시설 등등 나무랄데가 없는 곳이었습니다
  - 저는 여기서 이틀밖에 머물지 못해서 너무 슬픕니다.
- source_sentence: 일본종교문화를 잘 이해하시는 분이 이용하시는 게 좋아요
  sentences:
  - 그리고 슬리퍼를 하나 준비하시는 게 좋아요
  - 위치도 역에서 도보 5분정도로 아주 좋습니다.
  - 깨끗하긴 한데, 식기 세척기에서 냄새가 좀 나요.
pipeline_tag: sentence-similarity
library_name: sentence-transformers
metrics:
- pearson_cosine
- spearman_cosine
model-index:
- name: SentenceTransformer based on snunlp/KR-SBERT-V40K-klueNLI-augSTS
  results:
  - task:
      type: semantic-similarity
      name: Semantic Similarity
    dataset:
      name: sts dev
      type: sts-dev
    metrics:
    - type: pearson_cosine
      value: 0.8482819342480793
      name: Pearson Cosine
    - type: spearman_cosine
      value: 0.8555072181319486
      name: Spearman Cosine
---

# SentenceTransformer based on snunlp/KR-SBERT-V40K-klueNLI-augSTS

This is a [sentence-transformers](https://www.SBERT.net) model finetuned from [snunlp/KR-SBERT-V40K-klueNLI-augSTS](https://huggingface.co/snunlp/KR-SBERT-V40K-klueNLI-augSTS). It maps sentences & paragraphs to a 768-dimensional dense vector space and can be used for semantic textual similarity, semantic search, paraphrase mining, text classification, clustering, and more.

## Model Details

### Model Description
- **Model Type:** Sentence Transformer
- **Base model:** [snunlp/KR-SBERT-V40K-klueNLI-augSTS](https://huggingface.co/snunlp/KR-SBERT-V40K-klueNLI-augSTS) <!-- at revision 92c6c2c7032f680bff0f9f0c63fadd3f97e635b2 -->
- **Maximum Sequence Length:** 128 tokens
- **Output Dimensionality:** 768 dimensions
- **Similarity Function:** Cosine Similarity
<!-- - **Training Dataset:** Unknown -->
<!-- - **Language:** Unknown -->
<!-- - **License:** Unknown -->

### Model Sources

- **Documentation:** [Sentence Transformers Documentation](https://sbert.net)
- **Repository:** [Sentence Transformers on GitHub](https://github.com/UKPLab/sentence-transformers)
- **Hugging Face:** [Sentence Transformers on Hugging Face](https://huggingface.co/models?library=sentence-transformers)

### Full Model Architecture

```
SentenceTransformer(
  (0): Transformer({'max_seq_length': 128, 'do_lower_case': False}) with Transformer model: BertModel 
  (1): Pooling({'word_embedding_dimension': 768, 'pooling_mode_cls_token': False, 'pooling_mode_mean_tokens': True, 'pooling_mode_max_tokens': False, 'pooling_mode_mean_sqrt_len_tokens': False, 'pooling_mode_weightedmean_tokens': False, 'pooling_mode_lasttoken': False, 'include_prompt': True})
)
```

## Usage

### Direct Usage (Sentence Transformers)

First install the Sentence Transformers library:

```bash
pip install -U sentence-transformers
```

Then you can load this model and run inference.
```python
from sentence_transformers import SentenceTransformer

# Download from the 🤗 Hub
model = SentenceTransformer("sentence_transformers_model_id")
# Run inference
sentences = [
    '일본종교문화를 잘 이해하시는 분이 이용하시는 게 좋아요',
    '그리고 슬리퍼를 하나 준비하시는 게 좋아요',
    '위치도 역에서 도보 5분정도로 아주 좋습니다.',
]
embeddings = model.encode(sentences)
print(embeddings.shape)
# [3, 768]

# Get the similarity scores for the embeddings
similarities = model.similarity(embeddings, embeddings)
print(similarities.shape)
# [3, 3]
```

<!--
### Direct Usage (Transformers)

<details><summary>Click to see the direct usage in Transformers</summary>

</details>
-->

<!--
### Downstream Usage (Sentence Transformers)

You can finetune this model on your own dataset.

<details><summary>Click to expand</summary>

</details>
-->

<!--
### Out-of-Scope Use

*List how the model may foreseeably be misused and address what users ought not to do with the model.*
-->

## Evaluation

### Metrics

#### Semantic Similarity

* Dataset: `sts-dev`
* Evaluated with [<code>EmbeddingSimilarityEvaluator</code>](https://sbert.net/docs/package_reference/sentence_transformer/evaluation.html#sentence_transformers.evaluation.EmbeddingSimilarityEvaluator)

| Metric              | Value      |
|:--------------------|:-----------|
| pearson_cosine      | 0.8483     |
| **spearman_cosine** | **0.8555** |

<!--
## Bias, Risks and Limitations

*What are the known or foreseeable issues stemming from this model? You could also flag here known failure cases or weaknesses of the model.*
-->

<!--
### Recommendations

*What are recommendations with respect to the foreseeable issues? For example, filtering explicit content.*
-->

## Training Details

### Training Dataset

#### Unnamed Dataset

* Size: 5,371 training samples
* Columns: <code>sentence1</code>, <code>sentence2</code>, and <code>score</code>
* Approximate statistics based on the first 1000 samples:
  |         | sentence1                                                                         | sentence2                                                                         | score                                                          |
  |:--------|:----------------------------------------------------------------------------------|:----------------------------------------------------------------------------------|:---------------------------------------------------------------|
  | type    | string                                                                            | string                                                                            | float                                                          |
  | details | <ul><li>min: 8 tokens</li><li>mean: 14.45 tokens</li><li>max: 38 tokens</li></ul> | <ul><li>min: 7 tokens</li><li>mean: 13.93 tokens</li><li>max: 40 tokens</li></ul> | <ul><li>min: 0.0</li><li>mean: 0.52</li><li>max: 1.0</li></ul> |
* Samples:
  | sentence1                                     | sentence2                                               | score                     |
  |:----------------------------------------------|:--------------------------------------------------------|:--------------------------|
  | <code>숙소 위치는 찾기 쉽고 일반적인 한국의 반지하 숙소입니다.</code> | <code>숙박시설의 위치는 쉽게 찾을 수 있고 한국의 대표적인 반지하 숙박시설입니다.</code> | <code>0.7428571429</code> |
  | <code>호스트의 답장이 늦으나, 개선될 것으로 보입니다.</code>      | <code>호스트 응답이 늦었지만 개선될 것으로 보입니다.</code>                 | <code>0.9428571429</code> |
  | <code>지하철을 타도 30분안에는 이동이 가능합니다!</code>        | <code>지하철을 탄다고 해도, 30분이면 그곳에 도착할 수 있어요!</code>          | <code>0.8</code>          |
* Loss: [<code>CosineSimilarityLoss</code>](https://sbert.net/docs/package_reference/sentence_transformer/losses.html#cosinesimilarityloss) with these parameters:
  ```json
  {
      "loss_fct": "torch.nn.modules.loss.MSELoss"
  }
  ```

### Evaluation Dataset

#### Unnamed Dataset

* Size: 255 evaluation samples
* Columns: <code>sentence1</code>, <code>sentence2</code>, and <code>score</code>
* Approximate statistics based on the first 255 samples:
  |         | sentence1                                                                         | sentence2                                                                         | score                                                         |
  |:--------|:----------------------------------------------------------------------------------|:----------------------------------------------------------------------------------|:--------------------------------------------------------------|
  | type    | string                                                                            | string                                                                            | float                                                         |
  | details | <ul><li>min: 9 tokens</li><li>mean: 13.52 tokens</li><li>max: 30 tokens</li></ul> | <ul><li>min: 8 tokens</li><li>mean: 13.15 tokens</li><li>max: 23 tokens</li></ul> | <ul><li>min: 0.0</li><li>mean: 0.5</li><li>max: 1.0</li></ul> |
* Samples:
  | sentence1                              | sentence2                                       | score                     |
  |:---------------------------------------|:------------------------------------------------|:--------------------------|
  | <code>무엇보다도 호스트분들이 너무 친절하셨습니다.</code>  | <code>무엇보다도, 호스트들은 매우 친절했습니다.</code>            | <code>0.9714285714</code> |
  | <code>주요 관광지 모두 걸어서 이동가능합니다.</code>    | <code>위치는 피렌체 중심가까지 걸어서 이동 가능합니다.</code>        | <code>0.2857142857</code> |
  | <code>다만, 도로와 인접해서 거리의 소음이 들려요.</code> | <code>하지만, 길과 가깝기 때문에 거리의 소음을 들을 수 있습니다.</code> | <code>0.7428571429</code> |
* Loss: [<code>CosineSimilarityLoss</code>](https://sbert.net/docs/package_reference/sentence_transformer/losses.html#cosinesimilarityloss) with these parameters:
  ```json
  {
      "loss_fct": "torch.nn.modules.loss.MSELoss"
  }
  ```

### Training Hyperparameters
#### Non-Default Hyperparameters

- `eval_strategy`: steps
- `per_device_train_batch_size`: 16
- `per_device_eval_batch_size`: 16
- `num_train_epochs`: 1
- `warmup_ratio`: 0.1

#### All Hyperparameters
<details><summary>Click to expand</summary>

- `overwrite_output_dir`: False
- `do_predict`: False
- `eval_strategy`: steps
- `prediction_loss_only`: True
- `per_device_train_batch_size`: 16
- `per_device_eval_batch_size`: 16
- `per_gpu_train_batch_size`: None
- `per_gpu_eval_batch_size`: None
- `gradient_accumulation_steps`: 1
- `eval_accumulation_steps`: None
- `torch_empty_cache_steps`: None
- `learning_rate`: 5e-05
- `weight_decay`: 0.0
- `adam_beta1`: 0.9
- `adam_beta2`: 0.999
- `adam_epsilon`: 1e-08
- `max_grad_norm`: 1.0
- `num_train_epochs`: 1
- `max_steps`: -1
- `lr_scheduler_type`: linear
- `lr_scheduler_kwargs`: {}
- `warmup_ratio`: 0.1
- `warmup_steps`: 0
- `log_level`: passive
- `log_level_replica`: warning
- `log_on_each_node`: True
- `logging_nan_inf_filter`: True
- `save_safetensors`: True
- `save_on_each_node`: False
- `save_only_model`: False
- `restore_callback_states_from_checkpoint`: False
- `no_cuda`: False
- `use_cpu`: False
- `use_mps_device`: False
- `seed`: 42
- `data_seed`: None
- `jit_mode_eval`: False
- `use_ipex`: False
- `bf16`: False
- `fp16`: False
- `fp16_opt_level`: O1
- `half_precision_backend`: auto
- `bf16_full_eval`: False
- `fp16_full_eval`: False
- `tf32`: None
- `local_rank`: 0
- `ddp_backend`: None
- `tpu_num_cores`: None
- `tpu_metrics_debug`: False
- `debug`: []
- `dataloader_drop_last`: False
- `dataloader_num_workers`: 0
- `dataloader_prefetch_factor`: None
- `past_index`: -1
- `disable_tqdm`: False
- `remove_unused_columns`: True
- `label_names`: None
- `load_best_model_at_end`: False
- `ignore_data_skip`: False
- `fsdp`: []
- `fsdp_min_num_params`: 0
- `fsdp_config`: {'min_num_params': 0, 'xla': False, 'xla_fsdp_v2': False, 'xla_fsdp_grad_ckpt': False}
- `fsdp_transformer_layer_cls_to_wrap`: None
- `accelerator_config`: {'split_batches': False, 'dispatch_batches': None, 'even_batches': True, 'use_seedable_sampler': True, 'non_blocking': False, 'gradient_accumulation_kwargs': None}
- `deepspeed`: None
- `label_smoothing_factor`: 0.0
- `optim`: adamw_torch
- `optim_args`: None
- `adafactor`: False
- `group_by_length`: False
- `length_column_name`: length
- `ddp_find_unused_parameters`: None
- `ddp_bucket_cap_mb`: None
- `ddp_broadcast_buffers`: False
- `dataloader_pin_memory`: True
- `dataloader_persistent_workers`: False
- `skip_memory_metrics`: True
- `use_legacy_prediction_loop`: False
- `push_to_hub`: False
- `resume_from_checkpoint`: None
- `hub_model_id`: None
- `hub_strategy`: every_save
- `hub_private_repo`: None
- `hub_always_push`: False
- `gradient_checkpointing`: False
- `gradient_checkpointing_kwargs`: None
- `include_inputs_for_metrics`: False
- `include_for_metrics`: []
- `eval_do_concat_batches`: True
- `fp16_backend`: auto
- `push_to_hub_model_id`: None
- `push_to_hub_organization`: None
- `mp_parameters`: 
- `auto_find_batch_size`: False
- `full_determinism`: False
- `torchdynamo`: None
- `ray_scope`: last
- `ddp_timeout`: 1800
- `torch_compile`: False
- `torch_compile_backend`: None
- `torch_compile_mode`: None
- `dispatch_batches`: None
- `split_batches`: None
- `include_tokens_per_second`: False
- `include_num_input_tokens_seen`: False
- `neftune_noise_alpha`: None
- `optim_target_modules`: None
- `batch_eval_metrics`: False
- `eval_on_start`: False
- `use_liger_kernel`: False
- `eval_use_gather_object`: False
- `average_tokens_across_devices`: False
- `prompts`: None
- `batch_sampler`: batch_sampler
- `multi_dataset_batch_sampler`: proportional

</details>

### Training Logs
| Epoch | Step | Training Loss | Validation Loss | sts-dev_spearman_cosine |
|:-----:|:----:|:-------------:|:---------------:|:-----------------------:|
| 0.25  | 84   | 0.0227        | 0.0425          | 0.7777                  |
| 0.5   | 168  | 0.0187        | 0.0333          | 0.8290                  |
| 0.75  | 252  | 0.0165        | 0.0301          | 0.8447                  |
| 1.0   | 336  | 0.0145        | 0.0273          | 0.8555                  |


### Framework Versions
- Python: 3.11.11
- Sentence Transformers: 3.4.1
- Transformers: 4.48.3
- PyTorch: 2.1.0+cpu
- Accelerate: 1.3.0
- Datasets: 3.3.2
- Tokenizers: 0.21.0

## Citation

### BibTeX

#### Sentence Transformers
```bibtex
@inproceedings{reimers-2019-sentence-bert,
    title = "Sentence-BERT: Sentence Embeddings using Siamese BERT-Networks",
    author = "Reimers, Nils and Gurevych, Iryna",
    booktitle = "Proceedings of the 2019 Conference on Empirical Methods in Natural Language Processing",
    month = "11",
    year = "2019",
    publisher = "Association for Computational Linguistics",
    url = "https://arxiv.org/abs/1908.10084",
}
```

<!--
## Glossary

*Clearly define terms in order to be accessible across audiences.*
-->

<!--
## Model Card Authors

*Lists the people who create the model card, providing recognition and accountability for the detailed work that goes into its construction.*
-->

<!--
## Model Card Contact

*Provides a way for people who have updates to the Model Card, suggestions, or questions, to contact the Model Card authors.*
-->