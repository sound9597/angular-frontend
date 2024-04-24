## Use Alpine Linux as base image
FROM alpine:latest

# Install Node.js and npm
RUN apk add --no-cache nodejs npm

# Set the working directory in the Docker image
WORKDIR /app

# Copy package.json and package-lock.json (if exists) to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Expose port (if your application runs on a specific port)
EXPOSE 4200

# Specify the command to start the application
CMD ["npm", "start"]
