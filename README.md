# MindChatBot

MindChatBot is a mental health support chatbot application designed to assist users with emotional tracking, journaling, and conversational support. The chatbot is powered by OpenAI's GPT model, integrated into a Spring Boot backend, with data stored in MongoDB. This application provides a conversational AI interface that helps users with emotional tracking, mental well-being, and journaling while saving the interaction history for improved context in subsequent chats.

## Features
- **Mood calendar**: Provides emotional tracking.
- **Journaling**: Users can write journal entries with timestamps.
- **Chat History**: All user interactions with the chatbot are saved to maintain context.
- **Personalized Conversations**: By storing and using previous interactions, the chatbot can provide more relevant responses based on past chats.
- **User Authentication**: Users can securely log in to track their progress and interactions.

## Tech Stack
- **Backend**: Spring Boot (Java)
- **Database**: MongoDB
- **AI**: OpenAI GPT-4 for conversational AI
- **Frontend**: (You may add details about your frontend stack if applicable)
- **Security**: Spring Security for user authentication
- **Reactive Programming**: Spring WebFlux (Reactor) for handling asynchronous interactions

## Getting Started

Follow these steps to set up and run the application locally:

### Prerequisites

Make sure you have the following installed:
- JDK 17 or later
- MongoDB (you can use MongoDB Atlas for a cloud instance or install it locally)
- Maven or Gradle (depending on your setup)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/MindChatBot.git
   cd MindChatBot
