# Saku Pintar

An adaptive social arithmetic learning medium for students with intellectual disabilities, powered by a Large Language Model (LLM) and designed with a User-Centered Design (UCD) approach. 

## Pipeline Overview

```
Student profile → Profiling Agent → Instructional parameters
                                              ↓
                                 Contextualization Agent
                                  (RAG + Contextual Retrieval)
                                              ↓
                                  Content Generator Agent
                                              ↓
                      Evaluator Agent (Self-Refine, max. 3 iterations)
                            ↓ not yet valid          ↓ valid
                    back to Generator        Quiz Generator Agent
                                                      ↓
                                          Learning material + quiz
```

The Self-Refine loop is controlled by the backend orchestrator, not by the model itself.

## Agents

| Agent | Description |
|---|---|
| **Profiling Agent** | Extracts seven instructional parameters (KI-01 to KI-07) from student assessments |
| **Contextualization Agent** | Retrieves relevant learning context using RAG with Contextual Retrieval |
| **Content Generator Agent** | Produces learning material matched to the student's cognitive level |
| **Evaluator Agent** | Assesses material quality and requests revisions (Self-Refine) |
| **Quiz Generator Agent** | Generates questions with cognitive constraints and structural validation |

## Repository Structure

```
saku-pintar/
├── Android/                 
│   └── app/
├── Backend/                
│   ├── app/                 
│   ├── generate_all.py     
│   ├── run.py               
│   ├── requirements.txt
│   └── .env.example
└── README.md
```

## Tech Stack

- **Android:** Kotlin, Jetpack Compose
- **Backend:** Python, FastAPI
- **Database:** MySQL
- **LLM:** Anthropic Claude API

## Requirements

- Python 3.10+
- MySQL 8.0+
- Android Studio (latest stable version)
- Anthropic API key

## Running the Backend

```bash
git clone https://github.com/ryunaptr/saku-pintar.git
cd saku-pintar/Backend

python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

cp .env.example .env      
python run.py             
```

Variables in `.env`: `ANTHROPIC_API_KEY`, `DB_HOST`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`. Match these to `.env.example`.

Database schema: [`docs/schema.sql`](docs/schema.sql) *(add once created)*.

## Running the Android App

1. Open the `Android/` folder in Android Studio.
2. Set the backend address in [BASE_URL LOCATION].
3. Run on an emulator or a physical device.

## Data Notice

Student data, psychological assessment documents, and credentials are **not included** in this repository for privacy reasons.

## License

[MIT License / adjust]