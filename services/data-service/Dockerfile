# Use a lightweight Python image
FROM python:3.11.4-slim

# Set the working directory
WORKDIR /app

# Copy the dependencies file
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application files
COPY . .

ENV PYTHONPATH=/app/src/app

# Set environment variables for Flask
ENV FLASK_APP=src.app
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_RUN_PORT=9881

# Expose the correct port
EXPOSE 9881

# Run the application
CMD ["flask", "run"]