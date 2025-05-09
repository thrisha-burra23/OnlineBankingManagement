<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Banking System - Welcome</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background-color: #f5f5f5;
        }
        .container {
            text-align: center;
            background-color: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 2rem;
        }
        .login-buttons {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        .btn {
            padding: 1rem 2rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1rem;
            text-decoration: none;
            transition: background-color 0.3s;
        }
        .btn-user {
            background-color: #4CAF50;
            color: white;
        }
        .btn-admin {
            background-color: #2196F3;
            color: white;
        }
        .btn-register {
            background-color: #FF9800;
            color: white;
        }
        .btn:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to Banking System</h1>
        <div class="login-buttons">
            <a href="./user/login.jsp" class="btn btn-user">User Login</a>
            <a href="./admin/login.jsp" class="btn btn-admin">Admin Login</a>
            <a href="user/register.jsp" class="btn btn-register">Register New Account</a>
        </div>
    </div>
</body>
</html>
