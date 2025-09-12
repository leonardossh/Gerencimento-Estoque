<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sistema de Estoque</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .menu { background: #f4f4f4; padding: 20px; border-radius: 5px; }
        .menu a { display: block; margin: 10px 0; text-decoration: none; color: #333; }
        .menu a:hover { color: #007bff; }
    </style>
</head>
<body>
    <h1>Sistema de Estoque</h1>
    <div class="menu">
        <h2>Menu Principal</h2>
        <a href="app?action=listarUsuarios">Listar Usuários</a>
        <a href="app?action=cadastrarForm">Cadastrar Usuário</a>
        <a href="app?action=listarProdutos">Listar Produtos</a>
        <a href="app?action=cadastrarProdutoForm">Cadastrar Produto</a>
        <a href="app?action=listarEmpresas">Listar Empresas</a>
        <a href="app?action=cadastrarEmpresaForm">Cadastrar Empresa</a>
    </div>
</body>
</html>